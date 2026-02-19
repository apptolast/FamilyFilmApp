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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.screens.detail.CustomStatusButton
import com.apptolast.familyfilmapp.ui.screens.discover.components.SwipeableMovieCard
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_EMPTY
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_LOADING
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIP_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_TO_WATCH_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_WATCHED_BUTTON

/**
 * Discover Screen - Tinder-style movie discovery
 * Users can swipe or tap buttons to mark movies as Watched or Want to Watch
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error as snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
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
                uiState.isLoading -> {
                    // Loading state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .testTag(TT_DISCOVER_LOADING),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.discover_loading),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                uiState.isOutOfMovies -> {
                    // No more movies
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .testTag(TT_DISCOVER_EMPTY),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.discover_no_more_movies),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.discover_swipe_left),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    // Movie card with buttons
                    uiState.currentMovie?.let { movie ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Swipeable Movie Card
                            SwipeableMovieCard(
                                movie = movie,
                                onSwipeLeft = viewModel::markAsWatched,
                                onSwipeRight = viewModel::markAsWantToWatch,
                                modifier = Modifier.weight(1f),
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Watched Button
                                CustomStatusButton(
                                    text = stringResource(R.string.discover_watched),
                                    icon = Icons.Default.Visibility,
                                    isSelected = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(TT_DISCOVER_WATCHED_BUTTON),
                                    onClick = viewModel::markAsWatched,
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Skip Button
                                TextButton(
                                    onClick = viewModel::skipMovie,
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .testTag(TT_DISCOVER_SKIP_BUTTON),
                                ) {
                                    Text(
                                        text = stringResource(R.string.discover_skip),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // To Watch Button
                                CustomStatusButton(
                                    text = stringResource(R.string.discover_want_to_watch),
                                    icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                                    isSelected = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(TT_DISCOVER_TO_WATCH_BUTTON),
                                    onClick = viewModel::markAsWantToWatch,
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
