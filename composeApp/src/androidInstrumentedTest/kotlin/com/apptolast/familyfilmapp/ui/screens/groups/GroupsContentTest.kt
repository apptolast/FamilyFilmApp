package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.testing.createFamilyFilmComposeRule
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_EMPTY_TEXT
import com.apptolast.familyfilmapp.utils.TT_GROUPS_FAB
import com.apptolast.familyfilmapp.utils.TT_GROUPS_GROUP_CARD
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class GroupsContentTest {

    @get:Rule
    val composeTestRule = createFamilyFilmComposeRule()

    private val testGroup = Group(
        id = "g1",
        ownerId = "u1",
        name = "Family",
        users = listOf("u1"),
        lastUpdated = null,
    )
    private val testUser = User(
        id = "u1",
        email = "owner@example.com",
        language = "en-US",
        photoUrl = "",
        username = "Owner",
    )

    @Test
    fun groupsContent_emptyState_displaysEmptyText() {
        setGroupsContent(state = GroupViewModel.GroupsState(isLoading = false))
        composeTestRule.onNodeWithTag(TT_GROUPS_EMPTY_TEXT).assertIsDisplayed()
    }

    @Test
    fun groupsContent_withGroups_displaysCard() {
        setGroupsContent(
            state = GroupViewModel.GroupsState(
                groups = listOf(testGroup),
                selectedGroupId = "g1",
                selectedGroupData = GroupViewModel.GroupData(
                    group = testGroup,
                    members = listOf(testUser),
                    mediaToWatch = emptyList(),
                    mediaWatched = emptyList(),
                    recommendedMedia = null,
                    currentUserId = "u1",
                ),
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithTag(TT_GROUPS_GROUP_CARD).assertIsDisplayed()
    }

    @Test
    fun groupsContent_fabClick_requestsCreateDialog() {
        var requestedDialog: GroupViewModel.GroupScreenDialogs? = null
        setGroupsContent(
            state = GroupViewModel.GroupsState(isLoading = false),
            onShowDialog = { requestedDialog = it },
        )
        composeTestRule.onNodeWithTag(TT_GROUPS_FAB).performClick()
        assertEquals(GroupViewModel.GroupScreenDialogs.CreateGroup, requestedDialog)
    }

    private fun setGroupsContent(
        state: GroupViewModel.GroupsState,
        onSelectGroup: (String) -> Unit = {},
        onShowDialog: (GroupViewModel.GroupScreenDialogs) -> Unit = {},
        onCreateGroup: (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                GroupsContent(
                    state = state,
                    onSelectGroup = onSelectGroup,
                    onShowDialog = onShowDialog,
                    onCreateGroup = onCreateGroup,
                )
            }
        }
    }
}
