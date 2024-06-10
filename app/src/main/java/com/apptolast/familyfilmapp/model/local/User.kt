package com.apptolast.familyfilmapp.model.local

data class User(val id: Int, val email: String, val language: String, val provider: String) {
    constructor() : this(
        id = -1,
        email = "",
        language = "",
        provider = "",
    )
}
