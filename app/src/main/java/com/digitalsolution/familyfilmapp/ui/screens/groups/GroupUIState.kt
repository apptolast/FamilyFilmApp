package com.digitalsolution.familyfilmapp.ui.screens.groups

import com.digitalsolution.familyfilmapp.model.local.Group

data class GroupUIState(
    val groups: List<Group>
) {
    constructor() : this(
        groups = emptyList()
    )
}
