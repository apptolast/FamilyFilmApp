package com.apptolast.familyfilmapp.ui.screens.groups.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf

data class GroupUiState(
    var checkedEditGroupName: Boolean,
    var groupTitleChange: String,
    var deleteGroupButtonVisibility: Boolean,
    var addMemberButtonVisibility: Boolean,
    var updateNameGroupVisibility: Boolean,
    var selectedGroup: MutableState<Int>,
) {
    constructor() : this(
        checkedEditGroupName = false,
        groupTitleChange = "",
        deleteGroupButtonVisibility = false,
        addMemberButtonVisibility = false,
        updateNameGroupVisibility = false,
        selectedGroup = mutableIntStateOf(0),
    )
}
