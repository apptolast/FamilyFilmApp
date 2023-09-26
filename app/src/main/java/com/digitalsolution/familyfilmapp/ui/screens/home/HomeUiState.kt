package com.digitalsolution.familyfilmapp.ui.screens.home

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Movie

data class HomeUiState(
    val seen: List<Movie>,
    val forSeen: List<Movie>,
    val groups: List<String>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?
) : BaseUiState {

    constructor() : this(
        seen = emptyList(),
        forSeen = emptyList(),
        groups = emptyList(),
        isLoading = false,
        errorMessage = null,
    )
}
