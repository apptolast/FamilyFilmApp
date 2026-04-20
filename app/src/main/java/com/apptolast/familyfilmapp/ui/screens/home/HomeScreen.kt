package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.MediaFilterChips
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_HOME_MOVIE_ITEM
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_FIELD
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_LABEL
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onClickNav: (String) -> Unit = {},
) {
    val stateUI by viewModel.homeUiState.collectAsStateWithLifecycle()
    val mediaItems: LazyPagingItems<Media> = viewModel.media.collectAsLazyPagingItems()
    val nativeAds by viewModel.nativeAds.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val errorMessage = stateUI.errorMessage?.error

    val animatedColor by animateColorAsState(
        targetValue = lerp(
            MaterialTheme.colorScheme.surface.copy(alpha = 1f),
            Color.Transparent,
            scrollBehavior.state.collapsedFraction,
        ),
        label = "Color transition",
    )

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 8.dp),
        ) {
            HomeContent(
                mediaItems = mediaItems,
                nativeAds = nativeAds,
                onMediaClick = { media ->
                    onClickNav(DetailNavTypeDestination.getDestination(media))
                },
                searchMediaByName = viewModel::searchMediaByName,
                onFilterSelect = viewModel::setMediaFilter,
                stateUI = stateUI,
            )

            LoadStateContent(
                mediaItems = mediaItems,
                triggerError = viewModel::triggerError,
            )
        }
    }
}

@Composable
fun HomeContent(
    stateUI: HomeUiState,
    mediaItems: LazyPagingItems<Media>,
    modifier: Modifier = Modifier,
    nativeAds: List<NativeAd> = emptyList(),
    onMediaClick: (Media) -> Unit = {},
    searchMediaByName: (String) -> Unit = {},
    onFilterSelect: (com.apptolast.familyfilmapp.model.local.types.MediaFilter) -> Unit = {},
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        // Fixed header: search + filter chips with opaque background
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag(TT_HOME_SEARCH_TEXT_FIELD),
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchMediaByName(it)
                },
                shape = MaterialTheme.shapes.small,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "")
                },
                trailingIcon = {
                    AnimatedVisibility(searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                searchMediaByName("")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Borrar texto",
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = stringResource(R.string.search_film_or_series),
                        modifier = Modifier.testTag(TT_HOME_SEARCH_TEXT_LABEL),
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchMediaByName(searchQuery)
                    },
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    errorContainerColor = MaterialTheme.colorScheme.background,
                ),
            )

            MediaFilterChips(
                selectedFilter = stateUI.selectedFilter,
                onFilterSelect = onFilterSelect,
            )
        }

        // Scrollable media grid
        MediaGridList(
            mediaItems = mediaItems,
            stateUi = stateUI,
            nativeAds = nativeAds,
            onMediaClick = onMediaClick,
        )
    }
}

/** 7 media items + 1 native ad = block of 8 slots. */
private const val AD_INTERVAL = 8
private const val MEDIA_PER_AD = AD_INTERVAL - 1

@Composable
private fun MediaGridList(
    mediaItems: LazyPagingItems<Media>,
    stateUi: HomeUiState,
    nativeAds: List<NativeAd> = emptyList(),
    onMediaClick: (Media) -> Unit = {},
) {
    val filterMedia = stateUi.filterMedia
    val isFiltering = filterMedia.isNotEmpty()
    val ads = if (stateUi.user.hasRemovedAds) emptyList() else nativeAds

    // Source of truth: either the in-memory filter list or the paged flow
    val mediaCount = if (isFiltering) filterMedia.size else mediaItems.itemCount
    val adCount = if (ads.isEmpty()) 0 else mediaCount / MEDIA_PER_AD
    val totalSlots = mediaCount + adCount

    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
//        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(
            count = totalSlots,
            // Position-based keys: never read from `mediaItems` here (it runs in the snapshot
            // observer, which can race against paging refreshes and throw IndexOutOfBoundsException).
            key = { position ->
                if (isAdSlot(position, adCount)) "ad_$position" else "media_$position"
            },
            contentType = { position -> if (isAdSlot(position, adCount)) "ad" else "media" },
        ) { position ->
            if (isAdSlot(position, adCount)) {
                NativeAdItem(nativeAd = ads[(position / AD_INTERVAL) % ads.size])
            } else {
                val mediaIdx = position - (position / AD_INTERVAL).coerceAtMost(adCount)
                val media = if (isFiltering) {
                    filterMedia.getOrNull(mediaIdx)
                } else if (mediaIdx in 0 until mediaItems.itemCount) {
                    mediaItems[mediaIdx]
                } else {
                    null
                }
                media?.let {
                    MediaItem(
                        media = it,
                        onClick = onMediaClick,
                        modifier = Modifier.testTag("$TT_HOME_MOVIE_ITEM$mediaIdx"),
                    )
                }
            }
        }
    }
}

private fun isAdSlot(position: Int, adCount: Int): Boolean =
    (position + 1) % AD_INTERVAL == 0 && position / AD_INTERVAL < adCount

@Composable
private fun LoadStateContent(mediaItems: LazyPagingItems<Media>, triggerError: (String) -> Unit) {
    val currentTriggerError by rememberUpdatedState(triggerError)
    val refreshError = (mediaItems.loadState.refresh as? LoadState.Error)?.error
    val appendError = (mediaItems.loadState.append as? LoadState.Error)?.error

    LaunchedEffect(refreshError) {
        refreshError?.localizedMessage?.let { currentTriggerError(it) }
    }

    LaunchedEffect(appendError) {
        appendError?.localizedMessage?.let { currentTriggerError(it) }
    }

    when {
        mediaItems.loadState.refresh is LoadState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        mediaItems.loadState.append is LoadState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewHomeContent() {
    FamilyFilmAppTheme {
        HomeContent(
            mediaItems = flowOf(
                PagingData.from(
                    listOf(
                        Media().copy(
                            title = "Matrix",
                            overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                            """.trimIndent(),
                            posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                        ),
                    ),
                    sourceLoadStates = LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
                ),
            ).collectAsLazyPagingItems(),
            stateUI = HomeUiState().copy(
                filterMedia = arrayListOf(
                    Media(
                        title = "title",
                        posterPath = "",
                    ),
                ),
            ),
            onMediaClick = {},
            searchMediaByName = {},
        )
    }
}
