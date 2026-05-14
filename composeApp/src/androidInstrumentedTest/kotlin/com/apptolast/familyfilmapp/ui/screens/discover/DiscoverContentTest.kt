package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_EMPTY
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_MOVIE_CARD
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIP_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_TO_WATCH_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_WATCHED_BUTTON
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class DiscoverContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun discoverContent_emptyState_displaysEmptyTag() {
        setDiscoverContent(state = DiscoverUiState())
        composeTestRule.onNodeWithTag(TT_DISCOVER_EMPTY).assertIsDisplayed()
    }

    @Test
    fun discoverContent_withMedia_displaysCard() {
        setDiscoverContent(
            state = DiscoverUiState().copy(
                mediaList = listOf(Media(title = "Inception", posterPath = "")),
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithTag(TT_DISCOVER_MOVIE_CARD).assertIsDisplayed()
    }

    @Test
    fun discoverContent_skipClick_invokesCallback() {
        var skipped = false
        setDiscoverContent(
            state = DiscoverUiState().copy(
                mediaList = listOf(Media(title = "Inception", posterPath = "")),
                isLoading = false,
            ),
            onSkip = { skipped = true },
        )
        composeTestRule.onNodeWithTag(TT_DISCOVER_SKIP_BUTTON).performClick()
        assertEquals(true, skipped)
    }

    @Test
    fun discoverContent_watchedClick_invokesCallback() {
        var watched = false
        setDiscoverContent(
            state = DiscoverUiState().copy(
                mediaList = listOf(Media(title = "Inception", posterPath = "")),
                isLoading = false,
            ),
            onWatched = { watched = true },
        )
        composeTestRule.onNodeWithTag(TT_DISCOVER_WATCHED_BUTTON).performClick()
        assertEquals(true, watched)
    }

    @Test
    fun discoverContent_wantClick_invokesCallback() {
        var wanted = false
        setDiscoverContent(
            state = DiscoverUiState().copy(
                mediaList = listOf(Media(title = "Inception", posterPath = "")),
                isLoading = false,
            ),
            onWantToWatch = { wanted = true },
        )
        composeTestRule.onNodeWithTag(TT_DISCOVER_TO_WATCH_BUTTON).performClick()
        assertEquals(true, wanted)
    }

    private fun setDiscoverContent(
        state: DiscoverUiState,
        onSkip: () -> Unit = {},
        onWantToWatch: () -> Unit = {},
        onWatched: () -> Unit = {},
        onOpenDetails: (Media) -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                DiscoverContent(
                    state = state,
                    onSkip = onSkip,
                    onWantToWatch = onWantToWatch,
                    onWatched = onWatched,
                    onOpenDetails = onOpenDetails,
                )
            }
        }
    }
}
