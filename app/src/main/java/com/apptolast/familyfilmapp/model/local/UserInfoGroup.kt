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
