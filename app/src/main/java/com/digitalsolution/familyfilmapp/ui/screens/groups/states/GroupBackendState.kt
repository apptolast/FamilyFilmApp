package com.digitalsolution.familyfilmapp.ui.screens.groups.states

import com.digitalsolution.familyfilmapp.model.local.Group

data class GroupBackendState(
    val groupsInfo: List<Group>,
) {
    constructor() : this(
        groupsInfo = emptyList(),
    )
}
