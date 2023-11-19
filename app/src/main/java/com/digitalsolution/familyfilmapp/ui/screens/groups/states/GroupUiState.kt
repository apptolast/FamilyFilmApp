package com.digitalsolution.familyfilmapp.ui.screens.groups.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.model.local.Group

data class GroupUiState(
    var checkedEditGroupName: MutableState<Boolean>,
    var groupTitleChange: MutableState<String>,
    var deleteGroupButtonVisibility: MutableState<Boolean>,
    var groupSelected: Group,
) {
    constructor() : this(
        checkedEditGroupName = mutableStateOf(false),
        groupTitleChange = mutableStateOf(""),
        deleteGroupButtonVisibility = mutableStateOf(false),
        groupSelected = Group(),
    )
}
