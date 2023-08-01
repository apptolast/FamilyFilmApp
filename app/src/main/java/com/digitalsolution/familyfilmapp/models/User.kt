package com.digitalsolution.familyfilmapp.models

import java.util.Date

data class User(
    val username: String,
    val email: String,
    val id: String,
    val logInDate: Date,
)
