package com.digitalsolution.familyfilmapp.ui.screens.groups

import com.digitalsolution.familyfilmapp.model.local.GroupData

data class GroupUIState(
    val groups: List<GroupData>
) {
    constructor() : this(
        groups = emptyList()
    )
}
