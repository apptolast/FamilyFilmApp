/**
 * Cloud Function: revokeAppleToken (1st Gen)
 *
 * Callable function that revokes the Apple Sign-In token tied to the authenticated
 * Firebase user, so that the user-app relationship at Apple is severed when the user
 * deletes their account in the app.
 *
 * This is mandatory for App Store Review Guideline 5.1.1(v): if an app offers
 * Sign in with Apple and account creation, deleting the account must also revoke
 * the Apple authorization. Apple's reviewers verify this explicitly.
 *
 * Why this lives on the server:
 *   The Apple `/auth/revoke` endpoint requires `client_secret` — a short-lived JWT
 *   signed with the developer's private `.p8` ECDSA P-256 key. Embedding the `.p8`
 *   in a mobile binary is unsafe (extractable). The client therefore sends only
 *   the authorization code obtained from ASAuthorizationController, and the server
 *   performs the JWT signing + token exchange + revocation.
 *
 * Flow:
 *   1. Validate caller auth.
 *   2. Build the Apple client_secret JWT signed with APPLE_PRIVATE_KEY.
 *   3. Exchange `authorizationCode` for refresh_token via Apple /auth/token.
 *   4. POST refresh_token to Apple /auth/revoke.
 *   5. Return { success: true }. Errors throw HttpsError so the client can decide
 *      whether to abort the deletion or proceed regardless.
 *
 * Why 1st Gen: matches the existing chatComplete function. See its header for the
 * Domain-restricted-sharing rationale that prevents 2nd Gen callables here.
 */

import * as functions from "firebase-functions/v1";
import * as jwt from "jsonwebtoken";

interface RevokeAppleTokenRequest {
  authorizationCode: string;
}

interface RevokeAppleTokenResponse {
  success: true;
}

interface AppleTokenResponse {
  access_token?: string;
  refresh_token?: string;
  id_token?: string;
  token_type?: string;
  expires_in?: number;
  error?: string;
  error_description?: string;
}

const APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
const APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

function buildClientSecret(): string {
  const teamId = process.env.APPLE_TEAM_ID;
  const keyId = process.env.APPLE_KEY_ID;
  const servicesId = process.env.APPLE_SERVICES_ID;
  const privateKey = process.env.APPLE_PRIVATE_KEY;

  if (!teamId || !keyId || !servicesId || !privateKey) {
    throw new functions.https.HttpsError(
      "failed-precondition",
      "APPLE_SECRETS_MISSING",
    );
  }

  // Apple requires ES256-signed JWT, max 6 months validity.
  return jwt.sign({}, privateKey, {
    algorithm: "ES256",
    expiresIn: "180d",
    audience: "https://appleid.apple.com",
    issuer: teamId,
    subject: servicesId,
    keyid: keyId,
  });
}

async function exchangeCodeForRefreshToken(
  authorizationCode: string,
  clientSecret: string,
): Promise<string> {
  const body = new URLSearchParams({
    client_id: process.env.APPLE_SERVICES_ID || "",
    client_secret: clientSecret,
    code: authorizationCode,
    grant_type: "authorization_code",
  });

  const response = await fetch(APPLE_TOKEN_URL, {
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: body.toString(),
  });
  const json = (await response.json()) as AppleTokenResponse;

  if (!response.ok || json.error || !json.refresh_token) {
    throw new functions.https.HttpsError(
      "internal",
      `APPLE_TOKEN_EXCHANGE_FAILED: ${json.error || response.status} ${json.error_description || ""}`.trim(),
    );
  }
  return json.refresh_token;
}

async function revokeRefreshToken(
  refreshToken: string,
  clientSecret: string,
): Promise<void> {
  const body = new URLSearchParams({
    client_id: process.env.APPLE_SERVICES_ID || "",
    client_secret: clientSecret,
    token: refreshToken,
    token_type_hint: "refresh_token",
  });

  const response = await fetch(APPLE_REVOKE_URL, {
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: body.toString(),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new functions.https.HttpsError(
      "internal",
      `APPLE_REVOKE_FAILED: ${response.status} ${text}`.trim(),
    );
  }
  // Apple returns 200 OK with empty body on success.
}

export const revokeAppleToken = functions
  .region("europe-west2")
  .runWith({
    secrets: [
      "APPLE_PRIVATE_KEY",
      "APPLE_KEY_ID",
      "APPLE_TEAM_ID",
      "APPLE_SERVICES_ID",
    ],
    enforceAppCheck: true,
  })
  .https.onCall(
    async (
      data: RevokeAppleTokenRequest,
      context: functions.https.CallableContext,
    ): Promise<RevokeAppleTokenResponse> => {
      const uid = context.auth?.uid;
      if (!uid) {
        throw new functions.https.HttpsError(
          "unauthenticated",
          "AUTH_REQUIRED",
        );
      }

      const authorizationCode = (data?.authorizationCode || "").trim();
      if (!authorizationCode) {
        throw new functions.https.HttpsError(
          "invalid-argument",
          "AUTHORIZATION_CODE_REQUIRED",
        );
      }

      const clientSecret = buildClientSecret();
      const refreshToken = await exchangeCodeForRefreshToken(
        authorizationCode,
        clientSecret,
      );
      await revokeRefreshToken(refreshToken, clientSecret);

      return {success: true};
    },
  );
