/**
 * Cloud Function: revenueCatWebhook (HTTP, 1st Gen)
 *
 * Receives RevenueCat webhook events (INITIAL_PURCHASE, RENEWAL, CANCELLATION, etc.)
 * and mirrors the `chat_premium` entitlement state into Firestore under:
 *   users/{app_user_id}/entitlements/chat_premium
 *
 * The Cloud Function `chatComplete` then reads this mirror to decide the per-month
 * message limit (5 for free, 50 for premium). We persist the mirror in Firestore
 * because calling RevenueCat REST API on every request would add latency and reduce
 * reliability — Firestore reads are much cheaper and faster.
 *
 * Auth:
 *   We expect the `Authorization` header to carry a Bearer shared secret configured
 *   in the RevenueCat dashboard (Integrations → Webhooks → Authorization header).
 *   The secret is read from Secret Manager (`REVENUECAT_WEBHOOK_SECRET`) at runtime.
 *
 * Idempotency:
 *   The doc is `set(..., {merge: true})` keyed on `chat_premium`, so replaying an
 *   event is safe — we always end in a consistent state based on the latest event.
 */

import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";
import type {Request, Response} from "express";

if (admin.apps.length === 0) admin.initializeApp();

/** Events that grant / refresh the entitlement. */
const POSITIVE_EVENTS = new Set([
  "INITIAL_PURCHASE",
  "RENEWAL",
  "UNCANCELLATION",
  "PRODUCT_CHANGE",
  "NON_RENEWING_PURCHASE",
]);

/** Events that revoke the entitlement. */
const NEGATIVE_EVENTS = new Set([
  "CANCELLATION",
  "EXPIRATION",
  "SUBSCRIPTION_PAUSED",
]);

/** Events we log but otherwise ignore (no entitlement state change). */
const INFORMATIONAL_EVENTS = new Set([
  "BILLING_ISSUE",
  "SUBSCRIBER_ALIAS",
  "TRANSFER",
  "TEST",
]);

interface RevenueCatEvent {
  type: string;
  app_user_id?: string;
  original_app_user_id?: string;
  aliases?: string[];
  entitlement_ids?: string[];
  entitlement_id?: string;
  product_id?: string;
  expiration_at_ms?: number;
  event_timestamp_ms?: number;
}

interface RevenueCatPayload {
  event: RevenueCatEvent;
  api_version?: string;
}

export const revenueCatWebhook = functions
  .region("europe-west2")
  .runWith({
    secrets: ["REVENUECAT_WEBHOOK_SECRET"],
  })
  .https.onRequest(async (req: Request, res: Response): Promise<void> => {
    // 1. Only accept POST.
    if (req.method !== "POST") {
      res.status(405).send("Method Not Allowed");
      return;
    }

    // 2. Validate shared secret. RevenueCat sends it as "Authorization: <value>"
    //    (no Bearer prefix unless the dashboard is configured with one).
    const expected = process.env.REVENUECAT_WEBHOOK_SECRET;
    if (!expected) {
      console.error("REVENUECAT_WEBHOOK_SECRET is not configured");
      res.status(500).send("Server misconfigured");
      return;
    }
    const auth = req.header("Authorization") ?? "";
    if (auth !== expected && auth !== `Bearer ${expected}`) {
      console.warn("revenueCatWebhook rejected: bad Authorization header");
      res.status(401).send("Unauthorized");
      return;
    }

    // 3. Parse payload.
    const body = req.body as RevenueCatPayload | undefined;
    const event = body?.event;
    if (!event || !event.type) {
      res.status(400).send("Missing event");
      return;
    }
    console.log(
      `RC event=${event.type} user=${event.app_user_id} productId=${event.product_id}`,
    );

    // 4. Ignore informational events.
    if (INFORMATIONAL_EVENTS.has(event.type)) {
      res.status(200).send("Ignored");
      return;
    }

    // 5. Ensure we have an app_user_id that matches a Firebase uid.
    //    The client must call Purchases.logIn(firebaseUid) so these align.
    const appUserId = event.app_user_id ?? event.original_app_user_id;
    if (!appUserId) {
      res.status(400).send("Missing app_user_id");
      return;
    }

    // 6. Only act if this event relates to the `chat_premium` entitlement.
    const entitlements = event.entitlement_ids
      ?? (event.entitlement_id ? [event.entitlement_id] : []);
    if (!entitlements.includes("chat_premium")) {
      console.log(`Ignoring event — not targeting chat_premium entitlement`);
      res.status(200).send("Ignored (not chat_premium)");
      return;
    }

    // 7. Decide the new state.
    let isActive: boolean;
    if (POSITIVE_EVENTS.has(event.type)) {
      isActive = true;
    } else if (NEGATIVE_EVENTS.has(event.type)) {
      isActive = false;
    } else {
      console.log(`Unhandled event type '${event.type}' — no mirror update`);
      res.status(200).send("Ignored (unhandled event type)");
      return;
    }

    // 8. Mirror to Firestore.
    const expiresAt = event.expiration_at_ms
      ? admin.firestore.Timestamp.fromMillis(event.expiration_at_ms)
      : null;

    await admin
      .firestore()
      .doc(`users/${appUserId}/entitlements/chat_premium`)
      .set(
        {
          isActive,
          expiresAt,
          productId: event.product_id ?? null,
          source: "revenuecat",
          lastEventType: event.type,
          updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        },
        {merge: true},
      );

    console.log(
      `chat_premium mirror updated for uid=${appUserId}: isActive=${isActive}`,
    );
    res.status(200).send("OK");
  });
