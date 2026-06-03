@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.toGroupMediaStatusTable
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

/**
 * Regression tests for the group movie-status sync (the "all members show 0/0" bug).
 * They pin the atomic reconcile path and the one-shot self-heal, and that the destructive
 * delete-then-insert is gone.
 */
class RepositoryImplSyncTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repositoryScope = CoroutineScope(testDispatcher)

    private val room = mock<RoomDatasource>(MockMode.autoUnit)
    private val firebase = mock<FirebaseDatabaseDatasource>(MockMode.autoUnit)
    private val tmdb = mock<TmdbDatasource>(MockMode.autoUnit)
    private val localeManager = mock<TmdbLocaleManager>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)

    private val userId = "hgarcia"
    private val group = Group(
        id = "g1",
        ownerId = "john",
        name = "G1",
        users = listOf("john", "hgarcia"),
        lastUpdated = null,
    )

    private val johnWatched = GroupMediaStatus("g1", "john", 100, MediaStatus.Watched, MediaType.MOVIE)
    private val johnToWatch = GroupMediaStatus("g1", "john", 200, MediaStatus.ToWatch, MediaType.MOVIE)
    private val hgarciaWatched = GroupMediaStatus("g1", "hgarcia", 300, MediaStatus.Watched, MediaType.MOVIE)
    private val groupStatuses = listOf(johnWatched, johnToWatch, hgarciaWatched)
    private val groupStatusTables = groupStatuses.map { it.toGroupMediaStatusTable() }

    private fun setUpCommonStubs() {
        everySuspend { firebase.getUserById(userId) } returns null
        every { firebase.getMyGroups(userId) } returns flowOf(listOf(group))
        every { room.getMyGroups(userId) } returns flowOf(emptyList<GroupTable>())
    }

    private fun createRepository() = RepositoryImpl(room, firebase, tmdb, repositoryScope, localeManager, crashReporter)

    @AfterTest
    fun tearDown() {
        repositoryScope.cancel()
    }

    @Test
    fun `populated emission reconciles atomically without destructive delete-then-insert`() = runTest(testDispatcher) {
        setUpCommonStubs()
        every { firebase.observeMovieStatusesForGroup("g1") } returns flowOf(groupStatuses)
        everySuspend { firebase.getMovieStatusesForGroupOnce("g1") } returns groupStatuses

        createRepository().startSync(userId)
        advanceUntilIdle()

        // The atomic reconcile carries every member's statuses (john's 6/7 survive alongside hgarcia's).
        verifySuspend(VerifyMode.atLeast(1)) {
            room.reconcileMovieStatusesForGroup("g1", groupStatusTables)
        }
        // The old destructive path must be gone.
        verifySuspend(VerifyMode.not) { room.insertAllMovieStatuses(any()) }
        verifySuspend(VerifyMode.not) { room.deleteMovieStatusesByGroup(any()) }
    }

    @Test
    fun `self-heal reconciles from one-shot fetch even when the listener stays silent`() = runTest(testDispatcher) {
        setUpCommonStubs()
        // Listener never emits (e.g. Firestore subcollection unchanged after a transient CASCADE wipe).
        every { firebase.observeMovieStatusesForGroup("g1") } returns emptyFlow()
        everySuspend { firebase.getMovieStatusesForGroupOnce("g1") } returns groupStatuses

        createRepository().startSync(userId)
        advanceUntilIdle()

        verifySuspend(VerifyMode.atLeast(1)) {
            room.reconcileMovieStatusesForGroup("g1", groupStatusTables)
        }
    }

    @Test
    fun `group leaving the active set cancels its sync job and clears its rows`() = runTest(testDispatcher) {
        everySuspend { firebase.getUserById(userId) } returns null
        // First emission creates the per-group job; the second drops the group entirely.
        every { firebase.getMyGroups(userId) } returns flowOf(listOf(group), emptyList<Group>())
        every { room.getMyGroups(userId) } returns flowOf(emptyList<GroupTable>())
        every { firebase.observeMovieStatusesForGroup("g1") } returns flowOf(groupStatuses)
        everySuspend { firebase.getMovieStatusesForGroupOnce("g1") } returns groupStatuses

        createRepository().startSync(userId)
        advanceUntilIdle()

        verifySuspend(VerifyMode.atLeast(1)) { room.deleteMovieStatusesByGroup("g1") }
    }
}
