package com.digitalsolution.familyfilmapp.ui.screens.search.states

import androidx.compose.runtime.MutableState

data class SearchScreenUI(
    val searchQuery: MutableState<String>,
    val searchResults: MutableState<String>,
)
