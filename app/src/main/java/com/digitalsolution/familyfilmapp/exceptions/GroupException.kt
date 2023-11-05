package com.digitalsolution.familyfilmapp.exceptions

sealed class GroupException(
    message: String,
) : CustomException(message) {

    data class AddGroup(
        val value: String = "Group can't be created",
    ) : GroupException(value)
}
