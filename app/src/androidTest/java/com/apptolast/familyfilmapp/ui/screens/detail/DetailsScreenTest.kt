package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DETAIL_OVERVIEW
import com.apptolast.familyfilmapp.utils.TT_DETAIL_TITLE
import org.junit.Rule
import org.junit.Test

@MediumTest
class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testMedia = Media().copy(
        id = 100,
        title = "The Matrix",
        overview = "A computer programmer discovers reality is a simulation.",
        releaseDate = "1999-03-31",
        adult = false,
        voteAverage = 8.7f,
    )

    private val adultMedia = Media().copy(
        id = 200,
        title = "Adult Film",
        overview = "An adult-rated movie.",
        releaseDate = "2020-01-01",
        adult = true,
        voteAverage = 5.0f,
    )

    @Test
    fun mediaInfo_displaysTitle() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                MediaInfo(media = testMedia)
            }
        }
        composeTestRule.onNodeWithTag(TT_DETAIL_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_DETAIL_TITLE).assertTextContains("The Matrix")
    }

    @Test
    fun mediaInfo_displaysOverview() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                MediaInfo(media = testMedia)
            }
        }
        composeTestRule.onNodeWithTag(TT_DETAIL_OVERVIEW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_DETAIL_OVERVIEW)
            .assertTextContains("A computer programmer discovers reality is a simulation.")
    }

    @Test
    fun mediaInfo_displaysReleaseYear() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                MediaInfo(media = testMedia)
            }
        }
        composeTestRule.onNodeWithText("1999").assertIsDisplayed()
    }

    @Test
    fun mediaInfo_displaysAdultBadge() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                MediaInfo(media = adultMedia)
            }
        }
        composeTestRule.onNodeWithText("+18").assertIsDisplayed()
    }

    @Test
    fun mediaInfo_hidesAdultBadgeForNonAdult() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                MediaInfo(media = testMedia)
            }
        }
        composeTestRule.onNodeWithText("+18").assertDoesNotExist()
    }
}
