package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_EMPTY_TEXT
import com.apptolast.familyfilmapp.utils.TT_GROUPS_FAB
import com.apptolast.familyfilmapp.utils.TT_GROUPS_GROUP_CARD
import com.apptolast.familyfilmapp.utils.TT_GROUPS_TAB
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

@MediumTest
class GroupsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser1 = User().copy(id = "user-1", email = "alice@test.com")
    private val testUser2 = User().copy(id = "user-2", email = "bob@test.com")

    private val testGroup1 = Group().copy(
        id = "group-1",
        ownerId = "user-1",
        name = "Movie Nights",
        users = listOf("user-1", "user-2"),
    )
    private val testGroup2 = Group().copy(
        id = "group-2",
        ownerId = "user-1",
        name = "Family Films",
        users = listOf("user-1"),
    )

    private val testGroupData = GroupViewModel.GroupData(
        group = testGroup1,
        members = listOf(testUser1, testUser2),
        moviesToWatch = listOf(
            Movie().copy(id = 1, title = "Inception", overview = "A thief enters dreams"),
            Movie().copy(id = 2, title = "Matrix", overview = "A programmer discovers reality is fake"),
        ),
        moviesWatched = listOf(
            Movie().copy(id = 3, title = "Interstellar", overview = "Space exploration"),
        ),
        recommendedMovie = Movie().copy(id = 1, title = "Inception", voteAverage = 8.8f),
        currentUserId = "user-1",
    )

    private fun setGroupContent(
        groupData: GroupViewModel.GroupData = testGroupData,
        groups: List<Group> = listOf(testGroup1, testGroup2),
        selectedGroupIndex: Int = 0,
        syncState: SyncState = SyncState.Synced,
        onGroupSelect: (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                GroupContent(
                    groupData = groupData,
                    groups = groups,
                    selectedGroupIndex = selectedGroupIndex,
                    syncState = syncState,
                    scrollState = rememberLazyListState(),
                    onGroupSelect = onGroupSelect,
                )
            }
        }
    }

    @Test
    fun groupContent_displaysGroupTabs() {
        setGroupContent()
        composeTestRule.onNodeWithTag("${TT_GROUPS_TAB}_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TT_GROUPS_TAB}_1").assertIsDisplayed()
    }

    @Test
    fun groupContent_displaysGroupCard() {
        setGroupContent()
        composeTestRule.onNodeWithTag(TT_GROUPS_GROUP_CARD).assertIsDisplayed()
    }

    @Test
    fun groupContent_displaysMemberEmails() {
        setGroupContent()
        composeTestRule.onNodeWithText("alice@test.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("bob@test.com").assertIsDisplayed()
    }

    @Test
    fun groupContent_emptyGroupsShowsCreateMessage() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                GroupContent(
                    groupData = testGroupData,
                    groups = emptyList(),
                    selectedGroupIndex = 0,
                    syncState = SyncState.Synced,
                    scrollState = rememberLazyListState(),
                )
            }
        }
        composeTestRule.onNodeWithTag(TT_GROUPS_EMPTY_TEXT).assertIsDisplayed()
    }

    @Test
    fun groupContent_tabSelectionCallsCallback() {
        var selectedGroupId = ""
        setGroupContent(onGroupSelect = { selectedGroupId = it })
        composeTestRule.onNodeWithTag("${TT_GROUPS_TAB}_1").performClick()
        assertThat(selectedGroupId).isEqualTo("group-2")
    }

    @Test
    fun expandableFAB_displaysFABWithText() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                ExpandableFAB(isExtended = true, onClick = {})
            }
        }
        composeTestRule.onNodeWithTag(TT_GROUPS_FAB).assertIsDisplayed()
    }
}
