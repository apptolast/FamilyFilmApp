package com.apptolast.familyfilmapp.ui.screens.groups.states

data class GroupUiState(
    var checkedEditGroupName: Boolean,
    var groupTitleChange: String,
    var deleteGroupButtonVisibility: Boolean,
    var addMemberButtonVisibility: Boolean,
    var updateNameGroupVisibility: Boolean,
) {
    constructor() : this(
        checkedEditGroupName = false,
        groupTitleChange = "",
        deleteGroupButtonVisibility = false,
        addMemberButtonVisibility = false,
        updateNameGroupVisibility = false,
    )
}

val FAKE_GROUP_UI_STATE = listOf(

    GroupUiState(
        checkedEditGroupName = true,
        groupTitleChange = "",
        deleteGroupButtonVisibility = true,
        addMemberButtonVisibility = true,
        updateNameGroupVisibility = true
    )
)
