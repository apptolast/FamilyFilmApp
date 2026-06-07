package com.apptolast.familyfilmapp.ui.screens.home

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaFilter

data class HomeUiState(
    val user: User,
    val filterMedia: List<Media>,
    val selectedFilter: MediaFilter,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(),
        filterMedia = emptyList(),
        selectedFilter = MediaFilter.ALL,
        isLoading = false,
        errorMessage = null,
    )
}
