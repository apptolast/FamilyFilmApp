package com.digitalsolution.familyfilmapp.ui.screens.groups.states

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Group

data class GroupBackendState(
    val groupsInfo: List<Group>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {
    constructor() : this(
        groupsInfo = emptyList(),
        isLoading = false,
        errorMessage = null,
    )
}
