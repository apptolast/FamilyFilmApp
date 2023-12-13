package com.apptolast.familyfilmapp.exceptions

sealed class GroupException(
    message: String,
) : CustomException(message) {

    data class AddGroup(
        val value: String = "Group can't be created",
    ) : GroupException(value)

    data class DeleteGroup(
        val value: String = "Group can't be deleted",
    ) : GroupException(value)
}
