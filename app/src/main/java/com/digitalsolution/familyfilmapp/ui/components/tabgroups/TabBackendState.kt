package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Group

data class TabBackendState(
    val groups: List<Group>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {
    constructor() : this(
        groups = emptyList(),
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(isLoading = isLoading)
}
