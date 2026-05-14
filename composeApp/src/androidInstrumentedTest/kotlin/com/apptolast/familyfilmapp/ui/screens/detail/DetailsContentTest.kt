package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DETAIL_BACK_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DETAIL_OVERVIEW
import com.apptolast.familyfilmapp.utils.TT_DETAIL_POSTER
import com.apptolast.familyfilmapp.utils.TT_DETAIL_TITLE
import com.apptolast.familyfilmapp.utils.TT_DETAIL_TO_WATCH_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DETAIL_WATCHED_BUTTON
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class DetailsContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val baseState = DetailUiState().copy(
        // overview must be non-empty: assertIsDisplayed() fails on Text nodes
        // that produce a zero-sized layout (empty string → no measured bounds).
        media = Media(title = "Inception", posterPath = "")
            .copy(overview = "A thief who steals corporate secrets through dream-sharing technology."),
    )

    @Test
    fun detailsContent_displaysCoreElements() {
        setDetailsContent(state = baseState)
        composeTestRule.onNodeWithTag(TT_DETAIL_POSTER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_DETAIL_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_DETAIL_OVERVIEW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_DETAIL_BACK_BUTTON).assertIsDisplayed()
    }

    @Test
    fun detailsContent_backClick_invokesCallback() {
        var backInvoked = false
        setDetailsContent(state = baseState, onBack = { backInvoked = true })
        composeTestRule.onNodeWithTag(TT_DETAIL_BACK_BUTTON).performClick()
        assertEquals(true, backInvoked)
    }

    @Test
    fun detailsContent_toWatchClick_emitsToWatchStatus() {
        var captured: MediaStatus? = null
        setDetailsContent(state = baseState, onStatusClick = { captured = it })
        composeTestRule.onNodeWithTag(TT_DETAIL_TO_WATCH_BUTTON).performClick()
        assertEquals(MediaStatus.ToWatch, captured)
    }

    @Test
    fun detailsContent_watchedClick_emitsWatchedStatus() {
        var captured: MediaStatus? = null
        setDetailsContent(state = baseState, onStatusClick = { captured = it })
        composeTestRule.onNodeWithTag(TT_DETAIL_WATCHED_BUTTON).performClick()
        assertEquals(MediaStatus.Watched, captured)
    }

    private fun setDetailsContent(
        state: DetailUiState,
        onBack: () -> Unit = {},
        onStatusClick: (MediaStatus) -> Unit = {},
        onGroupToggle: (String, Boolean) -> Unit = { _, _ -> },
        onConfirm: () -> Unit = {},
        onDismissSheet: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                DetailsContent(
                    state = state,
                    onBack = onBack,
                    onStatusClick = onStatusClick,
                    onGroupToggle = onGroupToggle,
                    onConfirm = onConfirm,
                    onDismissSheet = onDismissSheet,
                )
            }
        }
    }
}
