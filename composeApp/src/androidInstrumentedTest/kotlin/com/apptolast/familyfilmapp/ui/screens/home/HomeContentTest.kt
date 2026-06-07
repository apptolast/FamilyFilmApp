package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import app.cash.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.testing.createFamilyFilmComposeRule
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_HOME_MOVIE_ITEM
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_FIELD
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class HomeContentTest {

    @get:Rule
    val composeTestRule = createFamilyFilmComposeRule()

    @Test
    fun homeContent_displaysSearchField() {
        setHomeContent()
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).assertIsDisplayed()
    }

    @Test
    fun homeContent_searchInput_propagates() {
        var captured = ""
        setHomeContent(onSearchQueryChange = { captured = it })
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performTextInput("Matrix")
        assertEquals("Matrix", captured)
    }

    @Test
    fun homeContent_displaysGridItemsWithIndexedTag() {
        // Unique ids: the LazyVerticalGrid key = { m -> m.id } and Media(title, posterPath)
        // defaults id = 0, so two items would collide.
        setHomeContent(
            media = listOf(
                Media(title = "Inception", posterPath = "").copy(id = 1),
                Media(title = "Arrival", posterPath = "").copy(id = 2),
            ),
        )
        composeTestRule.onNodeWithTag("${TT_HOME_MOVIE_ITEM}0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TT_HOME_MOVIE_ITEM}1").assertIsDisplayed()
    }

    @Test
    fun homeContent_mediaClick_invokesCallback() {
        var clicked: Media? = null
        val movie = Media(title = "Inception", posterPath = "").copy(id = 1)
        setHomeContent(
            media = listOf(movie),
            onMediaSelected = { clicked = it },
        )
        composeTestRule.onNodeWithTag("${TT_HOME_MOVIE_ITEM}0").performClick()
        assertEquals(movie.title, clicked?.title)
    }

    private fun setHomeContent(
        state: HomeUiState = HomeUiState(),
        media: List<Media> = emptyList(),
        searchQuery: String = "",
        onSearchQueryChange: (String) -> Unit = {},
        onFilterSelected: (MediaFilter) -> Unit = {},
        onMediaSelected: (Media) -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                HomeContent(
                    stateUI = state,
                    mediaItems = flowOf(PagingData.from(media)).collectAsLazyPagingItems(),
                    searchMediaByName = onSearchQueryChange,
                    onFilterSelect = onFilterSelected,
                    onMediaClick = onMediaSelected,
                )
            }
        }
    }
}
