/**
 * RevenueCat REST API helper (v1 `/subscribers/{app_user_id}`).
 *
 * Used as a fallback when the Firestore entitlement mirror is stale or missing.
 * The mirror is normally kept in sync by `revenueCatWebhook`, but webhooks are
 * asynchronous and can be delayed, misconfigured, or silently fail. Querying the
 * REST API directly is authoritative — we call it on the server only when the
 * mirror says non-premium, so premium users are not blocked by webhook lag.
 *
 * Auth: requires a **secret API key** from the RevenueCat Dashboard
 * (Project Settings → API Keys → "Secret API Keys"). This is different from
 * the public SDK key and from the webhook shared secret.
 *
 * Configure it once per environment:
 *   firebase functions:secrets:set REVENUECAT_API_KEY
 */

const REVENUECAT_BASE_URL = "https://api.revenuecat.com/v1";

interface RcEntitlement {
  expires_date: string | null; // ISO-8601, or null for lifetime
  grace_period_expires_date?: string | null; // billing-issue grace window
  product_identifier?: string;
  purchase_date?: string;
}

interface RcSubscriberResponse {
  subscriber?: {
    entitlements?: Record<string, RcEntitlement>;
  };
}

export interface RevenueCatEntitlementState {
  isActive: boolean;
  expiresAtMs: number | null;
  productId: string | null;
}

/**
 * Queries RevenueCat for the given `appUserId` and returns the state of the
 * `chat_premium` entitlement. Returns `{ isActive: false, ... }` if the
 * subscriber is unknown (404) or the entitlement is missing.
 *
 * Throws on transport or auth errors so the caller can decide whether to fail
 * open (trust the Firestore mirror and reject) or fail closed.
 */
export async function fetchChatPremiumFromRevenueCat(
  appUserId: string,
  entitlementId: string,
  apiKey: string,
): Promise<RevenueCatEntitlementState> {
  const url = `${REVENUECAT_BASE_URL}/subscribers/${encodeURIComponent(appUserId)}`;
  const response = await fetch(url, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${apiKey}`,
      "X-Platform": "android",
      "Accept": "application/json",
    },
  });

  if (response.status === 404) {
    // Subscriber never identified with RevenueCat — treat as non-premium.
    return {isActive: false, expiresAtMs: null, productId: null};
  }

  if (!response.ok) {
    const body = await response.text().catch(() => "<unreadable>");
    throw new Error(
      `RevenueCat REST ${response.status}: ${body.substring(0, 200)}`,
    );
  }

  const data = (await response.json()) as RcSubscriberResponse;
  const entitlement = data.subscriber?.entitlements?.[entitlementId];
  if (!entitlement) {
    return {isActive: false, expiresAtMs: null, productId: null};
  }

  // `null` expires_date means lifetime entitlement → always active.
  const expiresAtMs = entitlement.expires_date
    ? Date.parse(entitlement.expires_date)
    : null;
  // Grace period covers the window where RC keeps the entitlement live despite
  // a billing issue (Play/App Store retrying). If it's set and in the future,
  // the user is still entitled even if expires_date has passed.
  const gracePeriodMs = entitlement.grace_period_expires_date
    ? Date.parse(entitlement.grace_period_expires_date)
    : null;
  const now = Date.now();
  const isActive =
    expiresAtMs === null ||
    (Number.isFinite(expiresAtMs) && expiresAtMs > now) ||
    (gracePeriodMs !== null && Number.isFinite(gracePeriodMs) && gracePeriodMs > now);

  return {
    isActive,
    expiresAtMs: expiresAtMs && Number.isFinite(expiresAtMs) ? expiresAtMs : null,
    productId: entitlement.product_identifier ?? null,
  };
}
