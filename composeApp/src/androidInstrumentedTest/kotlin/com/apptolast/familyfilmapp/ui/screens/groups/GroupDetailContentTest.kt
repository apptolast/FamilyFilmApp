package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.testing.createFamilyFilmComposeRule
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_ADD_MEMBER
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_BACK
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_CONTENT
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_DELETE
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_EDIT
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_MEMBER_ROW
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_PROGRESS_DIALOG
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@MediumTest
class GroupDetailContentTest {

    @get:Rule
    val composeTestRule = createFamilyFilmComposeRule()

    private val owner = User("u1", "owner@example.com", "en-US", "", "Owner")
    private val member = User("u2", "member@example.com", "en-US", "", "Member")
    private val group = Group(
        id = "g1",
        ownerId = owner.id,
        name = "Family",
        users = listOf(owner.id, member.id),
        lastUpdated = null,
    )

    @Test
    fun groupDetailContent_displaysMemberRows() {
        setGroupDetailContent(state = detailState(currentUserId = owner.id))

        composeTestRule.onNodeWithTag(TT_GROUP_DETAIL_CONTENT).assertIsDisplayed()
        composeTestRule.onAllNodesWithTag(TT_GROUP_DETAIL_MEMBER_ROW).assertCountEquals(2)
    }

    @Test
    fun groupDetailContent_backClick_invokesCallback() {
        var clickedBack = false
        setGroupDetailContent(
            state = detailState(currentUserId = owner.id),
            onBack = { clickedBack = true },
        )

        composeTestRule.onNodeWithTag(TT_GROUP_DETAIL_BACK).performClick()

        assertTrue(clickedBack)
    }

    @Test
    fun groupDetailContent_ownerActions_requestDialogs() {
        val requestedDialogs = mutableListOf<GroupDetailDialog>()
        setGroupDetailContent(
            state = detailState(currentUserId = owner.id),
            onShowDialog = { requestedDialogs += it },
        )

        composeTestRule.onNodeWithTag(TT_GROUP_DETAIL_EDIT).performClick()
        composeTestRule.onNodeWithTag(TT_GROUP_DETAIL_ADD_MEMBER).performClick()
        composeTestRule.onNodeWithTag(TT_GROUP_DETAIL_DELETE).performClick()

        assertEquals(GroupDetailDialog.ChangeGroupName(group), requestedDialogs[0])
        assertEquals(GroupDetailDialog.AddMember(group), requestedDialogs[1])
        assertEquals(GroupDetailDialog.DeleteGroup(group), requestedDialogs[2])
    }

    @Test
    fun groupDetailContent_nonOwner_hidesOwnerActions() {
        setGroupDetailContent(state = detailState(currentUserId = member.id))

        composeTestRule.onAllNodesWithTag(TT_GROUP_DETAIL_EDIT).assertCountEquals(0)
        composeTestRule.onAllNodesWithTag(TT_GROUP_DETAIL_ADD_MEMBER).assertCountEquals(0)
        composeTestRule.onAllNodesWithTag(TT_GROUP_DETAIL_DELETE).assertCountEquals(0)
    }

    @Test
    fun groupDetailContent_loadingWithGroupData_displaysProgressDialog() {
        setGroupDetailContent(state = detailState(currentUserId = owner.id).copy(isLoading = true))

        composeTestRule.onNodeWithTag(TT_GROUP_DETAIL_PROGRESS_DIALOG).assertIsDisplayed()
    }

    private fun detailState(currentUserId: String): GroupDetailViewModel.GroupDetailState =
        GroupDetailViewModel.GroupDetailState(
            isLoading = false,
            groupData = GroupDetailViewModel.GroupData(
                group = group,
                members = listOf(owner, member),
                memberStats = mapOf(
                    owner.id to MemberMediaStats(watchedCount = 8, toWatchCount = 2),
                    member.id to MemberMediaStats(watchedCount = 10, toWatchCount = 1),
                ),
                mediaToWatch = emptyList(),
                mediaWatched = emptyList(),
                recommendedMedia = null,
                currentUserId = currentUserId,
            ),
        )

    private fun setGroupDetailContent(
        state: GroupDetailViewModel.GroupDetailState,
        onBack: () -> Unit = {},
        onShowDialog: (GroupDetailDialog) -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                GroupDetailContent(
                    state = state,
                    onBack = onBack,
                    onShowDialog = onShowDialog,
                )
            }
        }
    }
}
