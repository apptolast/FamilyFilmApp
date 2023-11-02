package com.digitalsolution.familyfilmapp.ui.screens.groups.states

import com.digitalsolution.familyfilmapp.model.local.GroupInfo

data class GroupBackendState(
    val groupsInfo: List<GroupInfo>,
    val addMemberInfoMessage: String,
) {
    constructor() : this(
        groupsInfo = emptyList(),
        addMemberInfoMessage = "",
    )
}
