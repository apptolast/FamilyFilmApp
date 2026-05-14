package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stub Discover screen. Shows the current media card title and the
 * three swipe-action buttons. The full SwipeableMediaCard (gesture
 * handling, fling, etc.) is a polish pass after migration.
 */
@Composable
fun DiscoverScreen(
    onMediaSelected: (mediaId: Int, mediaType: com.apptolast.familyfilmapp.model.local.types.MediaType) -> Unit,
    viewModel: DiscoverViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val current = state.currentMedia
        if (current == null) {
            Text(
                text = if (state.isLoading) "Loading…" else "No more media",
                style = MaterialTheme.typography.titleMedium,
            )
        } else {
            Text(text = current.title, style = MaterialTheme.typography.titleLarge)
            Text(text = current.overview, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.skipMedia() }) { Text("Skip") }
                Button(onClick = { viewModel.markAsWantToWatch() }) { Text("Want") }
                Button(onClick = { viewModel.markAsWatched() }) { Text("Seen") }
            }

            OutlinedButton(onClick = { onMediaSelected(current.id, current.mediaType) }) {
                Text("Details")
            }
        }
        state.errorMessage?.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
    }
}
