package com.apptolast.familyfilmapp.model.local

/**
 * Domain model for a single message in the Gemini chat conversation.
 *
 * Role is modelled as an enum (not boolean) to support a potential System/Tool role later
 * if we move to function calling or server-side prompts.
 */
data class ChatMessage(val id: String, val role: Role, val content: String, val timestamp: Long) {
    enum class Role { USER, ASSISTANT }
}
