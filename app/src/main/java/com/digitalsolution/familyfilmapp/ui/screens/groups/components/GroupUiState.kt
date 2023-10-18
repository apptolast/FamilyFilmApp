package com.digitalsolution.familyfilmapp.ui.screens.groups.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class GroupUiState(
    val checkedEditGroupName: MutableState<Boolean>,
    val groupTitleChange: MutableState<String>
) {
    constructor() : this(
        checkedEditGroupName = mutableStateOf(false),
        groupTitleChange = mutableStateOf(""),
    )

    constructor(
        checkedEditGroupName: Boolean
    ) : this(
        checkedEditGroupName = mutableStateOf(checkedEditGroupName),
        groupTitleChange = mutableStateOf("")
    )

    constructor(
        groupTitleChange: String
    ) : this(
        checkedEditGroupName = mutableStateOf(false),
        groupTitleChange = mutableStateOf(groupTitleChange)  // Envuelve groupTitleChange con mutableStateOf
    )


}
