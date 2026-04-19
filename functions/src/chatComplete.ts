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
 *   3. Read entitlement mirror (users/{uid}/entitlements/chat_premium) to decide limit.
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

const MODEL_NAME = "gemini-2.5-flash";
const FREE_LIMIT = 5;
const PREMIUM_LIMIT = 50;
const HISTORY_WINDOW = 20;
const MAX_PROMPT_CHARS = 4000;

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
    secrets: ["GEMINI_API_KEY"],
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

      // 3. Read entitlement (may be missing — treated as non-premium).
      const entitlementSnap = await db
        .doc(`users/${uid}/entitlements/chat_premium`)
        .get();
      const isPremium = Boolean(
        entitlementSnap.exists && entitlementSnap.get("isActive"),
      );
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
