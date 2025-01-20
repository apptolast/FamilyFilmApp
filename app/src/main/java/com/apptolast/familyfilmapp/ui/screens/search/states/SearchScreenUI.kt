package com.apptolast.familyfilmapp.ui.screens.search.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Movie

data class SearchScreenUI(
    val searchQuery: MutableState<String>,
    val searchResults: MutableState<List<Movie>>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        searchQuery = mutableStateOf(""),
        searchResults = mutableStateOf(emptyList()),
        isLoading = false,
        errorMessage = null,
    )
}
