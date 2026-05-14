package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.ui.components.MediaFilterChips
import com.apptolast.familyfilmapp.ui.screens.detail.CustomStatusButton
import com.apptolast.familyfilmapp.ui.screens.discover.components.SwipeableMediaCard
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_EMPTY
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_LOADING
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIP_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_TO_WATCH_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_WATCHED_BUTTON
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.discover_groups_all
import familyfilmkmp.composeapp.generated.resources.discover_groups_label
import familyfilmkmp.composeapp.generated.resources.discover_groups_select
import familyfilmkmp.composeapp.generated.resources.discover_loading
import familyfilmkmp.composeapp.generated.resources.discover_no_more_movies
import familyfilmkmp.composeapp.generated.resources.discover_skip
import familyfilmkmp.composeapp.generated.resources.discover_swipe_left
import familyfilmkmp.composeapp.generated.resources.discover_want_to_watch
import familyfilmkmp.composeapp.generated.resources.discover_watched
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverContent(
    state: DiscoverUiState,
    onSkip: () -> Unit,
    onWantToWatch: () -> Unit,
    onWatched: () -> Unit,
    onOpenDetails: (Media) -> Unit,
    onFilterSelect: (MediaFilter) -> Unit,
    onToggleGroup: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showGroupSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(16.dp),
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .testTag(TT_DISCOVER_LOADING),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(Res.string.discover_loading),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                state.isOutOfMedia -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .testTag(TT_DISCOVER_EMPTY),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.discover_no_more_movies),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.discover_swipe_left),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    state.currentMedia?.let { media ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            MediaFilterChips(
                                selectedFilter = state.selectedFilter,
                                onFilterSelect = onFilterSelect,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (state.groups.isNotEmpty()) {
                                val groupSummary = buildGroupSummaryText(state)

                                OutlinedButton(
                                    onClick = { showGroupSheet = true },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Groups,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = groupSummary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            key(media.id) {
                                SwipeableMediaCard(
                                    media = media,
                                    onSwipeLeft = onWatched,
                                    onSwipeRight = onWantToWatch,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CustomStatusButton(
                                    text = stringResource(Res.string.discover_watched),
                                    icon = Icons.Default.Visibility,
                                    isSelected = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(TT_DISCOVER_WATCHED_BUTTON),
                                    onClick = onWatched,
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                TextButton(
                                    onClick = onSkip,
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .testTag(TT_DISCOVER_SKIP_BUTTON),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.discover_skip),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                CustomStatusButton(
                                    text = stringResource(Res.string.discover_want_to_watch),
                                    icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                                    isSelected = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(TT_DISCOVER_TO_WATCH_BUTTON),
                                    onClick = onWantToWatch,
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showGroupSheet) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { showGroupSheet = false },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
            ) {
                Text(
                    text = stringResource(Res.string.discover_groups_select),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                state.groups.forEach { group ->
                    val isChecked = group.id in state.selectedGroupIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleGroup(group.id) },
                        )
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun buildGroupSummaryText(uiState: DiscoverUiState): String {
    val selectedGroups = uiState.groups.filter { it.id in uiState.selectedGroupIds }
    return when {
        selectedGroups.size == uiState.groups.size -> stringResource(Res.string.discover_groups_all)
        selectedGroups.isEmpty() -> stringResource(Res.string.discover_groups_select)
        selectedGroups.size == 1 -> stringResource(Res.string.discover_groups_label, selectedGroups.first().name)
        else -> stringResource(
            Res.string.discover_groups_label,
            "${selectedGroups.first().name}, +${selectedGroups.size - 1}",
        )
    }
}

@Composable
@Preview
private fun PreviewDiscoverContentEmpty() {
    FamilyFilmAppTheme {
        DiscoverContent(
            state = DiscoverUiState(),
            onSkip = {},
            onWantToWatch = {},
            onWatched = {},
            onOpenDetails = {},
            onFilterSelect = {},
            onToggleGroup = {},
            onClearError = {},
        )
    }
}

@Composable
@Preview
private fun PreviewDiscoverContentWithMedia() {
    FamilyFilmAppTheme {
        DiscoverContent(
            state = DiscoverUiState().copy(
                mediaList = listOf(Media(title = "Inception", posterPath = "")),
                isLoading = false,
            ),
            onSkip = {},
            onWantToWatch = {},
            onWatched = {},
            onOpenDetails = {},
            onFilterSelect = {},
            onToggleGroup = {},
            onClearError = {},
        )
    }
}
