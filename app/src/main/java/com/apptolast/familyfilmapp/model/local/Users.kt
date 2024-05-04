package com.apptolast.familyfilmapp.model.local

import timber.log.Timber

data class Users(
    val userId: Int,
    val email: String,
    val firebaseUuid: String,
    val role: RoleType,
) {
    constructor() : this(
        userId = -1,
        email = "",
        firebaseUuid = "",
        role = RoleType.USER,
    )
}

enum class RoleType {
    USER,
    ADMIN,
}

fun String?.toRoleType(): RoleType = when (this) {
    "USER" -> RoleType.USER
    "ADMIN" -> RoleType.ADMIN
    else -> {
        Timber.e("RoleType not found")
        RoleType.USER
    }
}
