package com.apptolast.familyfilmapp.ui.screens.groups.states

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Group

data class GroupBackendState(
    val groups: List<Group>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {
    constructor() : this(
        groups = listOf(Group()),
        isLoading = false,
        errorMessage = null,
    )
}
