package com.apptolast.familyfilmapp.exceptions

sealed interface GroupException : CustomException {

    data class AddGroup(override val error: String = "Group can't be created") : GroupException

    data class DeleteGroup(override val error: String = "Group can't be deleted") : GroupException

    data class UpdateGroupName(override val error: String = "Group name can't be updated") : GroupException

    data class AddMember(override val error: String = "Member can't be added to group") : GroupException

    data class DeleteUser(override val error: String = "Member can't be deleted from group") : GroupException
}
