package com.apptolast.familyfilmapp.exceptions

sealed class GroupException(message: String) : CustomException(message) {

    data class AddGroup(val value: String = "Group can't be created") : GroupException(value)

    data class DeleteGroup(val value: String = "Group can't be deleted") : GroupException(value)

    data class UpdateGroupName(val value: String = "Group name can't be updated") : GroupException(value)

    data class AddMember(val value: String = "Member can't be added to group") : GroupException(value)

    data class DeleteUser(val value: String = "Member can't be deleted from group") : GroupException(value)
}
