package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stub Home screen. Block 13 ships this as a list of popular media titles
 * pulled from the ViewModel; the rich legacy version (paging, native ad
 * slots, MediaFilterChips, NativeAdItem inserts every N items, etc.) is
 * a polish pass after migration.
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.homeUiState.collectAsState()
    val media by viewModel.media.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Welcome ${state.user.displayName}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Filter: ${state.selectedFilter.name}", style = MaterialTheme.typography.bodyMedium)
        if (state.isLoading) Text("Loading…")

        LazyColumn {
            items(media) { item ->
                Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
@Preview
private fun PreviewHomeScreen() {
    FamilyFilmAppTheme {}
}
