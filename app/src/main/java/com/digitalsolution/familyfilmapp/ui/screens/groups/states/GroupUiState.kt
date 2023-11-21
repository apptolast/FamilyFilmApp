package com.digitalsolution.familyfilmapp.ui.screens.groups.states

data class GroupUiState(
    var checkedEditGroupName: Boolean,
    var groupTitleChange: String,
    var deleteGroupButtonVisibility: Boolean,
) {
    constructor() : this(
        checkedEditGroupName = false,
        groupTitleChange = "",
        deleteGroupButtonVisibility = false,
    )
}
