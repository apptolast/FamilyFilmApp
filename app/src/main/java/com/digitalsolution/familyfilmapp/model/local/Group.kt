package com.digitalsolution.familyfilmapp.model.local

data class Group(
    val image: String,
    val name: String
) {
    constructor() : this(
        image = "",
        name = ""
    )
}
