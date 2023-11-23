package com.digitalsolution.familyfilmapp.model.local

data class UpdateGroupName(
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
