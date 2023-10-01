package com.digitalsolution.familyfilmapp.model.local

data class GroupData(
    val image: String,
    val name: String
) {
    constructor() : this(
        image = "",
        name = ""
    )
}
