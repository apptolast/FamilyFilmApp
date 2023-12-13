package com.apptolast.familyfilmapp.model.local

data class AddGroup(
    val id: Int,
    val name: String,
    val userId: Int,
) {
    constructor() : this(
        id = -1,
        name = "",
        userId = -1,
    )
}
