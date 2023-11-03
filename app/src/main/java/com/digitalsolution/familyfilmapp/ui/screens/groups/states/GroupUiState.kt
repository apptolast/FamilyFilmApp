package com.digitalsolution.familyfilmapp.ui.screens.groups.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class GroupUiState(
    var checkedEditGroupName: MutableState<Boolean>,
    var groupTitleChange: MutableState<String>,
) {
    constructor() : this(
        checkedEditGroupName = mutableStateOf(false),
        groupTitleChange = mutableStateOf(""),
    )
}
