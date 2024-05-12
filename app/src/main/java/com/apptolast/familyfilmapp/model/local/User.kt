package com.apptolast.familyfilmapp.model.local

data class User(
    val id: Int,
    val email: String,
    val language: Int,
    val provider: String,
){
    constructor() : this(
        id = -1,
        email = "",
        language = 0,
        provider = "",
    )
}
