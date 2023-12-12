package com.digitalsolution.familyfilmapp.ui.screens.search.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Movie

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

    override fun copyWithLoading(isLoading: Boolean): BaseUiState {
        TODO("Not yet implemented")
    }
}
