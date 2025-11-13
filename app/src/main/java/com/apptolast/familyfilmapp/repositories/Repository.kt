package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import com.apptolast.familyfilmapp.workers.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class RepositoryImpl @Inject constructor(
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
    private val workManager: WorkManager,
) : Repository {

    // /////////////////////////////////////////////////////////////////////////
    // Movies
    // /////////////////////////////////////////////////////////////////////////
    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { MoviePagingSource(tmdbDatasource) },
    ).flow

    override suspend fun getPopularMoviesList(page: Int): List<Movie> =
        tmdbDatasource.getPopularMovies(page).map { it.toDomain() }

    override suspend fun searchTmdbMovieByName(string: String): List<Movie> =
        tmdbDatasource.searchMovieByName(string).map { it.toDomain() }

    override suspend fun getMoviesByIds(ids: List<Int>): Result<List<Movie>> = runCatching {
        ids.map {
            tmdbDatasource.searchMovieById(it).toDomain()
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    override fun getMyGroups(userId: String): Flow<List<Group>> =
        roomDatasource.getMyGroups(userId).map { groupTables ->
            groupTables.map { it.toGroup() }
        }

    /**
     * Add new group into the database and store it in room database if successful.
     *
     * @param groupName The name of the group to be created.
     * @param user The user that is creating the group.
     */
    override fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.createGroup(
            groupName = groupName,
            user = user,
            success = success,
            failure = failure,
        )

    override fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.updateGroup(group, success = success, failure = failure)

    override fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.deleteGroup(group, success = success, failure = failure)

    override fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.addMember(group, email, success = success, failure = failure)

    override fun deleteMember(group: Group, user: User) = firebaseDatabaseDatasource.deleteMember(
        group,
        user,
        success = { enqueueSyncWork() },
        failure = { Timber.e(it, "Error deleting member from group") },
    )

    // /////////////////////////////////////////////////////////////////////////
    // Groups - New Suspend Functions
    // /////////////////////////////////////////////////////////////////////////

    override suspend fun createGroupSuspend(groupName: String, userId: String): Result<Group> {
        val user = runCatching {
            roomDatasource.getUser(userId).first()?.toUser()
        }.getOrNull()

        if (user == null) {
            return Result.failure(Exception("User not found"))
        }

        return suspendCancellableCoroutine { continuation ->
            firebaseDatabaseDatasource.createGroup(
                groupName = groupName,
                user = user,
                success = { group -> continuation.resume(Result.success(group)) },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    override suspend fun deleteGroupSuspend(groupId: String): Result<Unit> {
        val group = runCatching {
            roomDatasource.getGroupById(groupId).first()?.toGroup()
        }.getOrNull()

        if (group == null) {
            return Result.failure(Exception("Group not found"))
        }

        return suspendCancellableCoroutine { continuation ->
            firebaseDatabaseDatasource.deleteGroup(
                group = group,
                success = { continuation.resume(Result.success(Unit)) },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    override suspend fun addMemberSuspend(groupId: String, email: String): Result<Unit> {
        val group = runCatching {
            roomDatasource.getGroupById(groupId).first()?.toGroup()
        }.getOrNull()

        if (group == null) {
            return Result.failure(Exception("Group not found"))
        }

        return suspendCancellableCoroutine { continuation ->
            firebaseDatabaseDatasource.addMember(
                group = group,
                email = email,
                success = { continuation.resume(Result.success(Unit)) },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    override suspend fun removeMemberSuspend(groupId: String, userId: String): Result<Unit> {
        val group = runCatching {
            roomDatasource.getGroupById(groupId).first()?.toGroup()
        }.getOrNull()

        val user = runCatching {
            roomDatasource.getUser(userId).first()?.toUser()
        }.getOrNull()

        if (group == null || user == null) {
            return Result.failure(Exception("Group or user not found"))
        }

        return suspendCancellableCoroutine { continuation ->
            firebaseDatabaseDatasource.deleteMember(
                group = group,
                user = user,
                success = {
                    enqueueSyncWork()
                    continuation.resume(Result.success(Unit))
                },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    private fun enqueueSyncWork() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(syncWorkRequest)
    }

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    override fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.createUser(
            user = user,
            success = success,
            failure = failure,
        )

    override fun getUserById(userId: String): Flow<User> =
        roomDatasource.getUser(userId).filterNotNull().map { it.toUser() }

    override fun updateUser(user: User, success: (Void?) -> Unit) = firebaseDatabaseDatasource.updateUser(user, success)

    override fun deleteUser(user: User, success: () -> Unit, failure: (Exception) -> Unit) {
        firebaseDatabaseDatasource.deleteUser(
            user = user,
            success = success,
            failure = failure,
        )
    }

    override fun checkIfUserExists(userId: String, callback: (Boolean) -> Unit) {
        firebaseDatabaseDatasource.checkIfUserExists(userId, callback)
    }

    // /////////////////////////////////////////////////////////////////////////
    // Users - New Suspend Functions
    // /////////////////////////////////////////////////////////////////////////

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> = runCatching {
        if (userIds.isEmpty()) return@runCatching emptyList()

        // Try Room first (local cache)
        val userTables = roomDatasource.getUsersByIds(userIds)
        val roomUsers = userTables.map { it.toUser() }

        // If we got all users from Room, return them
        if (roomUsers.size == userIds.size) {
            Timber.d("All ${userIds.size} users found in Room cache")
            return@runCatching roomUsers
        }

        // Find missing user IDs and fetch from Firebase
        val foundIds = roomUsers.map { it.id }.toSet()
        val missingIds = userIds.filter { it !in foundIds }

        if (missingIds.isNotEmpty()) {
            Timber.d("Fetching ${missingIds.size} missing users from Firebase")
            val firebaseUsers = firebaseDatabaseDatasource.getUsersByIds(missingIds)

            // Save to Room for future use
            firebaseUsers.forEach { user ->
                roomDatasource.insertUser(user.toUserTable())
            }

            roomUsers + firebaseUsers
        } else {
            roomUsers
        }
    }
}

interface Repository {

    // Movies
    fun getPopularMovies(pageSize: Int = 1): Flow<PagingData<Movie>>
    suspend fun getPopularMoviesList(page: Int = 1): List<Movie>
    suspend fun searchTmdbMovieByName(string: String): List<Movie>
    suspend fun getMoviesByIds(ids: List<Int>): Result<List<Movie>>

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit)
    fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit)
    fun deleteMember(group: Group, user: User)

    // Groups - New suspend function versions
    suspend fun createGroupSuspend(groupName: String, userId: String): Result<Group>
    suspend fun deleteGroupSuspend(groupId: String): Result<Unit>
    suspend fun addMemberSuspend(groupId: String, email: String): Result<Unit>
    suspend fun removeMemberSuspend(groupId: String, userId: String): Result<Unit>

    // Users
    fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit)
    fun getUserById(userId: String): Flow<User>
    fun updateUser(user: User, success: (Void?) -> Unit)
    fun deleteUser(user: User, success: () -> Unit, failure: (Exception) -> Unit)
    fun checkIfUserExists(userId: String, callback: (Boolean) -> Unit)

    // Users - New suspend function versions
    suspend fun getUsersByIds(userIds: List<String>): Result<List<User>>
}
