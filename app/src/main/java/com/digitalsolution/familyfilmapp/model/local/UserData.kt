package com.digitalsolution.familyfilmapp.model.local

data class UserData(
    val email: String,
    val pass: String,
    val isLogin: Boolean,
    val isRegistered: Boolean
)
