package com.digitalsolution.familyfilmapp.model.local

data class AddMemberGroup(
    val userId: Int,
    val groupId: Int,
) {
    constructor() : this(
        userId = -1,
        groupId = -1,
    )
}
