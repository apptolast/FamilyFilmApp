package com.apptolast.familyfilmapp.model.local

data class UserInfoGroup(
    val id: Int,
    val email: String,
    val firebaseUUID: String,
    val role: String,
) {
    constructor() : this(
        id = -1,
        email = "",
        firebaseUUID = "",
        role = "",
    )
}

val FAKE_USER_INFO_GROUP = listOf(
    UserInfoGroup(1, "user1@example.com", "firebaseUUID1", "admin"),
    UserInfoGroup(2, "user2@example.com", "firebaseUUID2", "member"),
    UserInfoGroup(3, "user3@example.com", "firebaseUUID3", "member"),
    UserInfoGroup(4, "user4@example.com", "firebaseUUID4", "editor"),
    UserInfoGroup(5, "user5@example.com", "firebaseUUID5", "viewer")
)
