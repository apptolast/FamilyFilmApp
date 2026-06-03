package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.network.TmdbConfig
import com.apptolast.familyfilmapp.ui.components.MediaFilterChips
import com.apptolast.familyfilmapp.ui.screens.discover.components.SwipeableMediaCard
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_EMPTY
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_LOADING
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIPPED_EMPTY
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIPPED_RESTORE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIPPED_SHEET
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIP_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_TO_WATCH_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_WATCHED_BUTTON
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.discover_groups_all
import familyfilmkmp.composeapp.generated.resources.discover_groups_label
import familyfilmkmp.composeapp.generated.resources.discover_groups_select
import familyfilmkmp.composeapp.generated.resources.discover_loading
import familyfilmkmp.composeapp.generated.resources.discover_no_more_movies
import familyfilmkmp.composeapp.generated.resources.discover_restore
import familyfilmkmp.composeapp.generated.resources.discover_skip
import familyfilmkmp.composeapp.generated.resources.discover_skipped_empty
import familyfilmkmp.composeapp.generated.resources.discover_skipped_title
import familyfilmkmp.composeapp.generated.resources.discover_swipe_left
import familyfilmkmp.composeapp.generated.resources.discover_want_to_watch
import familyfilmkmp.composeapp.generated.resources.discover_watched
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverContent(
    state: DiscoverUiState,
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
    onWantToWatch: () -> Unit = {},
    onWatched: () -> Unit = {},
    onOpenDetails: (Media) -> Unit = {},
    onFilterSelect: (MediaFilter) -> Unit = {},
    onToggleGroup: (String) -> Unit = {},
    onDismissSkipped: () -> Unit = {},
    onRestoreSkipped: (Media) -> Unit = {},
    onClearError: () -> Unit = {},
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

                            key("${media.mediaType}:${media.id}") {
                                SwipeableMediaCard(
                                    media = media,
                                    onSwipeLeft = onWatched,
                                    onSwipeRight = onWantToWatch,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val neutralContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                val neutralContentColor = MaterialTheme.colorScheme.onSurface
                                val toWatchColor = MaterialTheme.colorScheme.error

                                DiscoverActionButton(
                                    text = stringResource(Res.string.discover_watched),
                                    icon = Icons.Default.Visibility,
                                    containerColor = neutralContainerColor,
                                    contentColor = neutralContentColor,
                                    labelColor = neutralContentColor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(TT_DISCOVER_WATCHED_BUTTON),
                                    onClick = onWatched,
                                )

                                DiscoverActionButton(
                                    text = stringResource(Res.string.discover_skip),
                                    icon = Icons.Default.Close,
                                    containerColor = neutralContainerColor,
                                    contentColor = neutralContentColor,
                                    labelColor = neutralContentColor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(TT_DISCOVER_SKIP_BUTTON),
                                    onClick = onSkip,
                                )

                                DiscoverActionButton(
                                    text = stringResource(Res.string.discover_want_to_watch),
                                    icon = Icons.Default.Favorite,
                                    containerColor = toWatchColor,
                                    contentColor = neutralContentColor,
                                    labelColor = toWatchColor,
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

    if (state.isSkippedSheetVisible) {
        val skippedSheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = onDismissSkipped,
            sheetState = skippedSheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TT_DISCOVER_SKIPPED_SHEET)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
            ) {
                Text(
                    text = stringResource(Res.string.discover_skipped_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                if (state.skippedMedia.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TT_DISCOVER_SKIPPED_EMPTY)
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.discover_skipped_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp),
                    ) {
                        items(
                            items = state.skippedMedia,
                            key = { media -> "${media.mediaType}:${media.id}" },
                        ) { media ->
                            SkippedMediaRow(
                                media = media,
                                onRestore = { onRestoreSkipped(media) },
                            )
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
private fun SkippedMediaRow(media: Media, onRestore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = if (media.posterPath.isEmpty()) {
                TmdbConfig.PLACEHOLDER_URL
            } else {
                "${TmdbConfig.POSTER_GRID}${media.posterPath}"
            },
            contentDescription = media.title,
            modifier = Modifier
                .width(56.dp)
                .height(84.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = media.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (media.releaseDate.isNotEmpty()) {
                Text(
                    text = media.releaseDate.take(4),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (media.overview.isNotEmpty()) {
                Text(
                    text = media.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        OutlinedButton(
            onClick = onRestore,
            modifier = Modifier.testTag(TT_DISCOVER_SKIPPED_RESTORE_BUTTON),
        ) {
            Text(text = stringResource(Res.string.discover_restore))
        }
    }
}

@Composable
private fun DiscoverActionButton(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(34.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = labelColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
        )
    }
}
