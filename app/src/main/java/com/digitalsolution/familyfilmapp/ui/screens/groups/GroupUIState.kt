package com.digitalsolution.familyfilmapp.ui.screens.groups

import com.digitalsolution.familyfilmapp.model.local.GroupInfo

data class GroupUIState(
    val groupsInfo: List<GroupInfo>
) {
    constructor() : this(
        groupsInfo = emptyList()
    )
}
