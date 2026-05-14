package com.apptolast.familyfilmapp.ui.screens.detail

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
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Stub Details screen. The legacy version (380 lines: poster header,
 * providers row, seasons/episodes meta, bottom sheet for status edit)
 * is deferred. This stub still exercises the Koin parametersOf route to
 * resolve the DetailsViewModel with the route payload from
 * Routes.Details.
 */
@Composable
fun DetailsScreen(
    mediaId: Int,
    mediaType: MediaType,
    onBack: () -> Unit,
    viewModel: DetailsViewModel = koinViewModel(parameters = { parametersOf(mediaId, mediaType) }),
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack) { Text("Back") }
        Text(text = state.media.title, style = MaterialTheme.typography.titleLarge)
        Text(text = state.media.overview, style = MaterialTheme.typography.bodyMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.onStatusButtonClick(MediaStatus.ToWatch) }) {
                Text(if (state.isToWatch) "Want ✓" else "Want")
            }
            Button(onClick = { viewModel.onStatusButtonClick(MediaStatus.Watched) }) {
                Text(if (state.isWatched) "Seen ✓" else "Seen")
            }
        }

        if (state.showBottomSheet) {
            Text(
                text = "Editing ${state.bottomSheetStatus} for ${state.selectedGroupIds.size} group(s)",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.onBottomSheetDismiss() }) { Text("Cancel") }
                Button(onClick = { viewModel.confirmMediaStatus() }) { Text("Confirm") }
            }
        }
    }
}
