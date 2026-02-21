package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.ui.screens.detail.CustomStatusButton
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_SKIP_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_TO_WATCH_BUTTON
import com.apptolast.familyfilmapp.utils.TT_DISCOVER_WATCHED_BUTTON
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

@MediumTest
class DiscoverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setDiscoverButtons(onWatched: () -> Unit = {}, onSkip: () -> Unit = {}, onToWatch: () -> Unit = {}) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomStatusButton(
                        text = "Watched",
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
                            text = "Skip",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomStatusButton(
                        text = "Want to Watch",
                        icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                        isSelected = false,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(TT_DISCOVER_TO_WATCH_BUTTON),
                        onClick = onToWatch,
                    )
                }
            }
        }
    }

    @Test
    fun discoverButtons_displaysWatchedButton() {
        setDiscoverButtons()
        composeTestRule.onNodeWithTag(TT_DISCOVER_WATCHED_BUTTON).assertIsDisplayed()
    }

    @Test
    fun discoverButtons_displaysSkipButton() {
        setDiscoverButtons()
        composeTestRule.onNodeWithTag(TT_DISCOVER_SKIP_BUTTON).assertIsDisplayed()
    }

    @Test
    fun discoverButtons_displaysToWatchButton() {
        setDiscoverButtons()
        composeTestRule.onNodeWithTag(TT_DISCOVER_TO_WATCH_BUTTON).assertIsDisplayed()
    }

    @Test
    fun discoverButtons_watchedCallsCallback() {
        var clicked = false
        setDiscoverButtons(onWatched = { clicked = true })
        composeTestRule.onNodeWithTag(TT_DISCOVER_WATCHED_BUTTON).performClick()
        assertThat(clicked).isTrue()
    }
}
