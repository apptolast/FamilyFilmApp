package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.homeUiState.collectAsState()
    val media by viewModel.media.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.searchMediaByName(searchQuery)
    }

    HomeContent(
        state = state,
        media = media,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onFilterSelected = viewModel::setMediaFilter,
        onMediaSelected = viewModel::logMediaSelected,
    )
}
