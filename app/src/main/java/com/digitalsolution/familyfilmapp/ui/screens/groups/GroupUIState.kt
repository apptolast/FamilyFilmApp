package com.digitalsolution.familyfilmapp.ui.screens.groups

import com.digitalsolution.familyfilmapp.model.local.GroupData

data class GroupUiState(
    val groups: List<GroupData>
) {
    constructor() : this(
        groups = emptyList()
    )
}
