package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Group

data class TabUIState(
    val selectedGroup: Int,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {
    constructor(): this(
        selectedGroup = -1,
        isLoading = false,
        errorMessage = null
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState {
        return this.copy(isLoading = isLoading)
    }
}
