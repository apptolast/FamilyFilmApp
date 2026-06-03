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
import com.apptolast.familyfilmapp.utils.TT_GROUPS_LIST_CARD
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
        users = listOf("u1", "u2"),
        lastUpdated = null,
    )
    private val users = listOf(
        User("u1", "owner@example.com", "en-US", "", "Owner"),
        User("u2", "member@example.com", "en-US", "", "Member"),
    )

    @Test
    fun groupsContent_emptyState_displaysEmptyText() {
        setGroupsContent(state = GroupsViewModel.GroupsState(isLoading = false))
        composeTestRule.onNodeWithTag(TT_GROUPS_EMPTY_TEXT).assertIsDisplayed()
    }

    @Test
    fun groupsContent_withGroups_displaysListCard() {
        setGroupsContent(state = groupsState())
        composeTestRule.onNodeWithTag(TT_GROUPS_LIST_CARD).assertIsDisplayed()
    }

    @Test
    fun groupsContent_groupClick_requestsOpenGroup() {
        var openedGroupId: String? = null
        setGroupsContent(
            state = groupsState(),
            onOpenGroup = { openedGroupId = it },
        )

        composeTestRule.onNodeWithTag(TT_GROUPS_LIST_CARD).performClick()

        assertEquals("g1", openedGroupId)
    }

    @Test
    fun groupsContent_fabClick_requestsCreateDialog() {
        var requestedDialog: GroupsScreenDialog? = null
        setGroupsContent(
            state = GroupsViewModel.GroupsState(isLoading = false),
            onShowDialog = { requestedDialog = it },
        )
        composeTestRule.onNodeWithTag(TT_GROUPS_FAB).performClick()
        assertEquals(GroupsScreenDialog.CreateGroup, requestedDialog)
    }

    private fun groupsState(): GroupsViewModel.GroupsState = GroupsViewModel.GroupsState(
        summaries = listOf(GroupSummary(testGroup, users)),
        isLoading = false,
    )

    private fun setGroupsContent(
        state: GroupsViewModel.GroupsState,
        onOpenGroup: (String) -> Unit = {},
        onShowDialog: (GroupsScreenDialog) -> Unit = {},
        onCreateGroup: (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                GroupsContent(
                    state = state,
                    onOpenGroup = onOpenGroup,
                    onShowDialog = onShowDialog,
                    onCreateGroup = onCreateGroup,
                )
            }
        }
    }
}
