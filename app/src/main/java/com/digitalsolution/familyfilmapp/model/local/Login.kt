package com.digitalsolution.familyfilmapp.model.local

data class Login(
    val email: String,
    val pass: String,
    val isLogin: Boolean,
    val isRegistered: Boolean
)
