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
