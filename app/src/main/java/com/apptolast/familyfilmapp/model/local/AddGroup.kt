package com.apptolast.familyfilmapp.model.local

data class AddGroup(
    val id: Int,
    val name: String,
) {
    constructor() : this(
        id = -1,
        name = "",
    )
}
