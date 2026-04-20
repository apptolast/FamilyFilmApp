/**
 * Cloud Function: chatComplete (1st Gen)
 *
 * Callable function that enforces the Gemini chat quota server-side and proxies the LLM call.
 *
 * We use 1st Gen instead of 2nd Gen on purpose: 2nd Gen runs on Cloud Run underneath and
 * requires granting `roles/run.invoker` to `allUsers` for Firebase Callable to work.
 * Google Workspace organizations (like apptolast.com) have the "Domain restricted sharing"
 * policy active by default, which blocks adding `allUsers` — making 2nd Gen callable impossible
 * without an org-level policy change. 1st Gen sidesteps the problem because it does not use
 * Cloud Run IAM; Firebase manages public access automatically.
 *
 * Flow:
 *   1. Validate caller auth + App Check token.
 *   2. Compute YYYY-MM key for current month.
 *   3. Resolve premium state: read the Firestore entitlement mirror; if negative,
 *      fall back to the RevenueCat REST API and repair the mirror on hit.
 *   4. In a Firestore transaction, read/increment users/{uid}/chat_usage/{YYYY-MM}.
 *   5. If quota exhausted, throw HttpsError("resource-exhausted") WITHOUT incrementing.
 *   6. Call Gemini with system instruction, history and prompt.
 *   7. Return { text, usage } to the client.
 *
 * Safety: the increment happens in a Firestore transaction atomically with the limit check,
 * so two concurrent requests cannot both succeed when only 1 slot remains. The transaction
 * commits BEFORE calling Gemini to simplify reasoning; if Gemini fails we do NOT refund the
 * slot (aligned with "we charge attempts"). To change policy to "charge only successes",
 * move the increment into a separate write after the Gemini call succeeds.
 */

import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";
import {GoogleGenAI} from "@google/genai";
import {fetchChatPremiumFromRevenueCat} from "./revenueCatApi";

const MODEL_NAME = "gemini-2.5-flash";
const FREE_LIMIT = 5;
const PREMIUM_LIMIT = 50;
const HISTORY_WINDOW = 20;
const MAX_PROMPT_CHARS = 4000;
const CHAT_PREMIUM_ENTITLEMENT = "chat_premium";

if (admin.apps.length === 0) admin.initializeApp();

interface ChatHistoryItem {
  role: "user" | "assistant";
  content: string;
}

interface ChatCompleteRequest {
  prompt: string;
  history: ChatHistoryItem[];
  languageTag: string;
  includeAdult: boolean;
}

interface ChatCompleteResponse {
  text: string;
  usage: {
    count: number;
    limit: number;
    isPremium: boolean;
  };
}

export const chatComplete = functions
  .region("europe-west2")
  .runWith({
    secrets: ["GEMINI_API_KEY", "REVENUECAT_API_KEY"],
    enforceAppCheck: true,
  })
  .https.onCall(
    async (
      data: ChatCompleteRequest,
      context: functions.https.CallableContext,
    ): Promise<ChatCompleteResponse> => {
      // 1. Auth
      const uid = context.auth?.uid;
      if (!uid) {
        throw new functions.https.HttpsError("unauthenticated", "AUTH_REQUIRED");
      }

      // 2. Validate and sanitize input
      if (!data || typeof data.prompt !== "string" || data.prompt.trim().length === 0) {
        throw new functions.https.HttpsError("invalid-argument", "PROMPT_REQUIRED");
      }
      if (data.prompt.length > MAX_PROMPT_CHARS) {
        throw new functions.https.HttpsError("invalid-argument", "PROMPT_TOO_LONG");
      }
      const prompt = data.prompt.trim();
      const languageTag = typeof data.languageTag === "string" ? data.languageTag : "en-US";
      const includeAdult = Boolean(data.includeAdult);
      const rawHistory = Array.isArray(data.history) ? data.history : [];
      const history = rawHistory
        .slice(-HISTORY_WINDOW)
        .filter(
          (m) =>
            m &&
            (m.role === "user" || m.role === "assistant") &&
            typeof m.content === "string",
        );

      const db = admin.firestore();
      const yearMonth = toYearMonth(new Date());

      // 3. Decide premium state.
      //
      // Primary source: the Firestore mirror at `users/{uid}/entitlements/chat_premium`,
      // kept in sync by `revenueCatWebhook`. Webhooks are asynchronous, so after a
      // fresh purchase the mirror may still be missing/stale while the client's
      // RevenueCat SDK already reports the entitlement as active. If we trusted
      // only the mirror, the user would see "Premium" in the UI but hit
      // QUOTA_EXCEEDED on their next message.
      //
      // Fallback: when the mirror says non-premium we query the RevenueCat REST
      // API for the authoritative state. If it confirms an active entitlement,
      // we repair the mirror so subsequent requests are fast, and we proceed
      // with the premium limit. This also self-heals webhook misconfiguration
      // and lost events.
      const isPremium = await resolveChatPremium(db, uid);
      const limit = isPremium ? PREMIUM_LIMIT : FREE_LIMIT;

      // 4. Atomic limit check + increment.
      const usageRef = db.doc(`users/${uid}/chat_usage/${yearMonth}`);
      const newCount = await db.runTransaction(async (tx) => {
        const snap = await tx.get(usageRef);
        const current = (snap.exists ? (snap.get("count") as number) : 0) ?? 0;
        if (current >= limit) {
          throw new functions.https.HttpsError("resource-exhausted", "QUOTA_EXCEEDED");
        }
        const next = current + 1;
        tx.set(
          usageRef,
          {
            count: next,
            limit,
            isPremium,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          },
          {merge: true},
        );
        return next;
      });

      // 5. Call Gemini server-side.
      const apiKey = process.env.GEMINI_API_KEY;
      if (!apiKey) {
        throw new functions.https.HttpsError("internal", "GEMINI_API_KEY_NOT_CONFIGURED");
      }
      const genAi = new GoogleGenAI({apiKey});
      const systemInstruction = buildSystemInstruction(languageTag, includeAdult);

      // Build Gemini contents: [...history, currentPrompt]. Map "assistant" -> "model".
      const contents = [
        ...history.map((m) => ({
          role: m.role === "assistant" ? "model" : "user",
          parts: [{text: m.content}],
        })),
        {role: "user", parts: [{text: prompt}]},
      ];

      let replyText = "";
      try {
        const result = await genAi.models.generateContent({
          model: MODEL_NAME,
          contents,
          config: {
            systemInstruction,
          },
        });
        replyText = result.text ?? "";
      } catch (err) {
        // We already charged the slot (policy above). Log and propagate as 'internal'.
        console.error("Gemini call failed after quota increment", err);
        throw new functions.https.HttpsError("internal", "GEMINI_FAILED");
      }

      if (!replyText) {
        throw new functions.https.HttpsError("internal", "GEMINI_EMPTY_RESPONSE");
      }

      return {
        text: replyText,
        usage: {count: newCount, limit, isPremium},
      };
    },
  );

/**
 * Resolves the current `chat_premium` state for `uid`, with a RevenueCat REST
 * API fallback when the Firestore mirror is negative or absent.
 *
 * Read path:
 *   1. Read `users/{uid}/entitlements/chat_premium` — if `isActive === true`,
 *      return true immediately (fast path, no network hop).
 *   2. Otherwise, call RevenueCat REST API. If REST returns active, mirror the
 *      state back to Firestore (same shape the webhook writes) so the next
 *      request hits the fast path.
 *   3. If REST says inactive, or if the REST call fails / no API key is
 *      configured, fall back to the mirror value (defaulting to non-premium).
 *      "Fail closed" is intentional: we prefer occasionally applying the free
 *      limit to a premium user over granting premium to a non-paying user.
 */
async function resolveChatPremium(
  db: admin.firestore.Firestore,
  uid: string,
): Promise<boolean> {
  const entitlementRef = db.doc(`users/${uid}/entitlements/${CHAT_PREMIUM_ENTITLEMENT}`);
  const entitlementSnap = await entitlementRef.get();
  if (entitlementSnap.exists && entitlementSnap.get("isActive") === true) {
    return true;
  }

  const apiKey = process.env.REVENUECAT_API_KEY;
  if (!apiKey) {
    // No fallback configured — trust the mirror as-is.
    console.warn(
      "REVENUECAT_API_KEY not configured; skipping REST fallback for chat_premium",
    );
    return false;
  }

  try {
    const remote = await fetchChatPremiumFromRevenueCat(
      uid,
      CHAT_PREMIUM_ENTITLEMENT,
      apiKey,
    );
    if (!remote.isActive) return false;

    // RevenueCat confirms premium — repair the Firestore mirror so we stop
    // hitting the REST API on every request for this user. Shape must match
    // what `revenueCatWebhook` writes so downstream code stays consistent.
    const expiresAt = remote.expiresAtMs
      ? admin.firestore.Timestamp.fromMillis(remote.expiresAtMs)
      : null;
    await entitlementRef.set(
      {
        isActive: true,
        expiresAt,
        productId: remote.productId,
        source: "rest-fallback",
        lastEventType: "REST_FALLBACK",
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      },
      {merge: true},
    );
    console.log(
      `resolveChatPremium: repaired mirror for uid=${uid} via REST fallback`,
    );
    return true;
  } catch (err) {
    console.error(
      `resolveChatPremium: REST fallback failed for uid=${uid}, falling back to mirror`,
      err,
    );
    return false;
  }
}

function toYearMonth(date: Date): string {
  const year = date.getUTCFullYear();
  const month = String(date.getUTCMonth() + 1).padStart(2, "0");
  return `${year}-${month}`;
}

function buildSystemInstruction(languageTag: string, includeAdult: boolean): string {
  const adultClause = includeAdult
    ? ""
    : "Do not recommend explicit adult content.";
  return [
    "You are a cinema and TV series expert.",
    "You only answer questions about films, series, actors, directors, genres and audiovisual recommendations.",
    "If the user asks about anything else, politely decline and redirect the conversation to cinema.",
    `Always answer in the user's language: ${languageTag}.`,
    "Be concise.",
    adultClause,
    "Format with light Markdown when helpful.",
  ]
    .filter((line) => line.length > 0)
    .join(" ");
}
