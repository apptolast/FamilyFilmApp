package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.WorkManager
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMovieStatus
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toGroupMovieStatus
import com.apptolast.familyfilmapp.model.room.toGroupMovieStatusTable
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class RepositoryImpl @Inject constructor(
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
    private val workManager: WorkManager,
    private val coroutineScope: CoroutineScope,
) : Repository {

    // Sync state management
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Synced)
    private var syncJob: Job? = null

    // /////////////////////////////////////////////////////////////////////////
    // Movies
    // /////////////////////////////////////////////////////////////////////////
    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { MoviePagingSource(tmdbDatasource) },
    ).flow

    override suspend fun getPopularMoviesList(page: Int): Result<List<Movie>> = runCatching {
        tmdbDatasource.getPopularMovies(page).map { it.toDomain() }
    }

    override suspend fun searchTmdbMovieByName(string: String): Result<List<Movie>> = runCatching {
        tmdbDatasource.searchMovieByName(string).map { it.toDomain() }
    }

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

    // /////////////////////////////////////////////////////////////////////////
    // Groups - Suspend Functions (Modern API)
    // /////////////////////////////////////////////////////////////////////////

    override suspend fun createGroup(groupName: String, userId: String): Result<Group> {
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
                success = { group ->
                    // Write-through: update Room before resuming to avoid race condition
                    coroutineScope.launch {
                        try {
                            roomDatasource.insertGroup(group.toGroupTable())
                            Timber.d("Group created and synced to Room: ${group.name}")
                        } catch (e: Exception) {
                            Timber.e(e, "Error syncing created group to Room")
                        }
                        continuation.resume(Result.success(group))
                    }
                },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    override suspend fun updateGroup(group: Group): Result<Unit> = suspendCancellableCoroutine { continuation ->
        firebaseDatabaseDatasource.updateGroup(
            group = group,
            success = {
                // Write-through: update Room before resuming to avoid race condition
                coroutineScope.launch {
                    try {
                        roomDatasource.insertGroup(group.toGroupTable())
                        Timber.d("Group updated and synced to Room: ${group.name}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error syncing updated group to Room")
                    }
                    continuation.resume(Result.success(Unit))
                }
            },
            failure = { error -> continuation.resume(Result.failure(error)) },
        )
    }

    override suspend fun deleteGroup(groupId: String): Result<Unit> {
        val group = runCatching {
            roomDatasource.getGroupById(groupId).first()?.toGroup()
        }.getOrNull()

        if (group == null) {
            return Result.failure(Exception("Group not found"))
        }

        return suspendCancellableCoroutine { continuation ->
            firebaseDatabaseDatasource.deleteGroup(
                group = group,
                success = {
                    // Write-through: delete from Room before resuming to avoid race condition
                    coroutineScope.launch {
                        try {
                            roomDatasource.deleteGroup(group.toGroupTable())
                            Timber.d("Group deleted and removed from Room: ${group.name}")
                        } catch (e: Exception) {
                            Timber.e(e, "Error removing deleted group from Room")
                        }
                        continuation.resume(Result.success(Unit))
                    }
                },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    override suspend fun addMember(groupId: String, email: String): Result<Unit> {
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
                success = {
                    // Write-through: look up user by email and update group in Room
                    coroutineScope.launch {
                        try {
                            val userTable = roomDatasource.getUserByEmail(email).first()
                            if (userTable != null) {
                                val updatedUsers = group.users + userTable.toUser().id
                                val updatedGroup = group.copy(users = updatedUsers)
                                roomDatasource.insertGroup(updatedGroup.toGroupTable())
                                Timber.d("Member added and group synced to Room: $email")
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error syncing added member to Room")
                        }
                        continuation.resume(Result.success(Unit))
                    }
                },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    override suspend fun removeMember(groupId: String, userId: String): Result<Unit> {
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
                    // Write-through: remove user from group's users list in Room
                    coroutineScope.launch {
                        try {
                            val updatedUsers = group.users - user.id
                            val updatedGroup = group.copy(users = updatedUsers)
                            roomDatasource.insertGroup(updatedGroup.toGroupTable())
                            Timber.d("Member removed and group synced to Room: ${user.email}")
                        } catch (e: Exception) {
                            Timber.e(e, "Error syncing removed member to Room")
                        }
                        continuation.resume(Result.success(Unit))
                    }
                },
                failure = { error -> continuation.resume(Result.failure(error)) },
            )
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    override suspend fun createUser(user: User): Result<Unit> = runCatching {
        firebaseDatabaseDatasource.createUser(user)
    }

    override fun getUserById(userId: String): Flow<User> =
        roomDatasource.getUser(userId).filterNotNull().map { it.toUser() }

    override suspend fun updateUser(user: User): Result<Unit> = suspendCancellableCoroutine { continuation ->
        firebaseDatabaseDatasource.updateUser(
            user = user,
            success = {
                // Write-through: update Room before resuming to avoid race condition
                coroutineScope.launch {
                    try {
                        roomDatasource.insertUser(user.toUserTable())
                        Timber.d("User updated and synced to Room: ${user.email}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error syncing updated user to Room")
                    }
                    continuation.resume(Result.success(Unit))
                }
            },
            failure = { error -> continuation.resume(Result.failure(error)) },
        )
    }

    override suspend fun deleteUser(user: User): Result<Unit> = runCatching {
        firebaseDatabaseDatasource.deleteUser(user)
    }

    override suspend fun checkIfUserExists(userId: String): Boolean = try {
        firebaseDatabaseDatasource.checkIfUserExists(userId)
    } catch (e: Exception) {
        Timber.e(e, "Error checking if user exists: $userId")
        false
    }

    override suspend fun isUsernameAvailable(username: String): Boolean = try {
        firebaseDatabaseDatasource.isUsernameAvailable(username)
    } catch (e: Exception) {
        Timber.e(e, "Error checking username availability")
        false // Fail closed: treat as unavailable on error
    }

    override suspend fun updateUsername(user: User, newUsername: String): Result<Unit> = runCatching {
        val oldUsername = user.username

        // Step 1: Atomically claim the new username in Firestore
        val claimed = firebaseDatabaseDatasource.claimUsername(newUsername, user.id)
        if (!claimed) throw Exception("Username '$newUsername' is already taken")

        // Step 2: Update the user document with the new username
        val updatedUser = user.copy(username = newUsername)
        suspendCancellableCoroutine { continuation ->
            firebaseDatabaseDatasource.updateUser(
                user = updatedUser,
                success = { continuation.resume(Unit) },
                failure = { e -> continuation.resume(throw e) },
            )
        }

        // Step 3: Release the old username slot (if they had one)
        if (!oldUsername.isNullOrBlank()) {
            firebaseDatabaseDatasource.releaseUsername(oldUsername)
        }

        // Step 4: Write-through to Room
        roomDatasource.insertUser(updatedUser.toUserTable())
        Timber.d("Username updated: ${user.id} -> $newUsername")
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

    // /////////////////////////////////////////////////////////////////////////
    // Movie Statuses (per-group)
    // /////////////////////////////////////////////////////////////////////////

    override suspend fun updateMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        status: MovieStatus,
    ): Result<Unit> = runCatching {
        // Write to Firebase first, then Room (write-through)
        for (groupId in groupIds) {
            firebaseDatabaseDatasource.setMovieStatus(groupId, userId, movieId, status)
            roomDatasource.insertMovieStatus(
                GroupMovieStatus(groupId, userId, movieId, status).toGroupMovieStatusTable(),
            )
        }
        Timber.d("Movie status updated in ${groupIds.size} groups: movie=$movieId, status=$status")
    }

    override suspend fun removeMovieStatus(groupIds: List<String>, userId: String, movieId: Int): Result<Unit> =
        runCatching {
            for (groupId in groupIds) {
                firebaseDatabaseDatasource.removeMovieStatus(groupId, userId, movieId)
                roomDatasource.deleteMovieStatus(groupId, userId, movieId)
            }
            Timber.d("Movie status removed from ${groupIds.size} groups: movie=$movieId")
        }

    override fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMovieStatus>> =
        roomDatasource.getMovieStatusesByGroup(groupId).map { tables ->
            tables.map { it.toGroupMovieStatus() }
        }

    override suspend fun getAllMarkedMovieIdsForUser(userId: String): List<Int> =
        roomDatasource.getAllMovieIdsForUser(userId)

    // /////////////////////////////////////////////////////////////////////////
    // Sync Lifecycle Management
    // /////////////////////////////////////////////////////////////////////////

    override fun startSync(userId: String) {
        Timber.d("Starting sync for user: $userId")

        // Cancel any existing sync job
        syncJob?.cancel()

        // Start new sync job
        syncJob = coroutineScope.launch {
            try {
                _syncState.value = SyncState.Syncing

                // First, sync the authenticated user to Room
                Timber.d("Syncing authenticated user to Room")
                val authenticatedUser = suspendCancellableCoroutine { continuation ->
                    firebaseDatabaseDatasource.getUserById(userId) { user ->
                        continuation.resume(user)
                    }
                }

                if (authenticatedUser != null) {
                    try {
                        roomDatasource.insertUser(authenticatedUser.toUserTable())
                        Timber.d("Authenticated user synced to Room: ${authenticatedUser.email}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error syncing authenticated user to Room")
                    }
                } else {
                    Timber.w("Authenticated user not found in Firebase")
                }

                // Start listening to Firebase groups and sync to Room
                firebaseDatabaseDatasource.getMyGroups(userId).collect { remoteGroups ->
                    Timber.d("Received ${remoteGroups.size} groups from Firebase")

                    val remoteGroupIds = remoteGroups.map { it.id }.toSet()

                    // Differential sync: delete local groups that no longer exist remotely
                    try {
                        val localGroups = roomDatasource.getMyGroups(userId).first()
                        localGroups.forEach { localGroup ->
                            if (localGroup.groupId !in remoteGroupIds) {
                                roomDatasource.deleteGroup(localGroup)
                                Timber.d("Deleted stale group from Room: ${localGroup.name}")
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error during differential sync cleanup")
                    }

                    // Upsert remote groups to Room
                    remoteGroups.forEach { group ->
                        try {
                            roomDatasource.insertGroup(group.toGroupTable())
                        } catch (e: Exception) {
                            Timber.e(e, "Error syncing group ${group.name} to Room")
                        }
                    }

                    // Migrate old statusMovies to per-group subcollections (one-time)
                    try {
                        firebaseDatabaseDatasource.migrateMovieStatusesIfNeeded(
                            userId,
                            remoteGroups,
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Error during movie status migration")
                    }

                    // Sync movie statuses from Firebase to Room for each group
                    remoteGroups.forEach { group ->
                        try {
                            syncMovieStatusesForGroup(group.id)
                        } catch (e: Exception) {
                            Timber.e(e, "Error syncing movie statuses for group: ${group.name}")
                        }
                    }

                    // Update sync state to synced
                    _syncState.value = SyncState.Synced
                }
            } catch (e: Exception) {
                Timber.e(e, "Sync error")
                _syncState.value = SyncState.Error(
                    message = e.message ?: "Unknown sync error",
                    throwable = e,
                )
            }
        }
    }

    private suspend fun syncMovieStatusesForGroup(groupId: String) {
        val remoteStatuses = firebaseDatabaseDatasource.observeMovieStatusesForGroup(groupId).first()
        // Replace all local statuses for this group with remote data
        roomDatasource.deleteMovieStatusesByGroup(groupId)
        if (remoteStatuses.isNotEmpty()) {
            roomDatasource.insertAllMovieStatuses(
                remoteStatuses.map { it.toGroupMovieStatusTable() },
            )
        }
        Timber.d("Synced ${remoteStatuses.size} movie statuses for group: $groupId")
    }

    override fun stopSync() {
        Timber.d("Stopping sync")
        syncJob?.cancel()
        syncJob = null
        _syncState.value = SyncState.Synced
    }

    override suspend fun clearLocalData() {
        Timber.d("Clearing all local data from Room")
        roomDatasource.clearAllData()
    }

    override fun getSyncState(): Flow<SyncState> = _syncState.asStateFlow()
}

interface Repository {

    // Movies
    fun getPopularMovies(pageSize: Int = 1): Flow<PagingData<Movie>>
    suspend fun getPopularMoviesList(page: Int = 1): Result<List<Movie>>
    suspend fun searchTmdbMovieByName(string: String): Result<List<Movie>>
    suspend fun getMoviesByIds(ids: List<Int>): Result<List<Movie>>

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    suspend fun createGroup(groupName: String, userId: String): Result<Group>
    suspend fun updateGroup(group: Group): Result<Unit>
    suspend fun deleteGroup(groupId: String): Result<Unit>
    suspend fun addMember(groupId: String, email: String): Result<Unit>
    suspend fun removeMember(groupId: String, userId: String): Result<Unit>

    // Users
    suspend fun createUser(user: User): Result<Unit>
    fun getUserById(userId: String): Flow<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(user: User): Result<Unit>
    suspend fun checkIfUserExists(userId: String): Boolean
    suspend fun getUsersByIds(userIds: List<String>): Result<List<User>>
    suspend fun isUsernameAvailable(username: String): Boolean
    suspend fun updateUsername(user: User, newUsername: String): Result<Unit>

    // Movie Statuses (per-group)
    suspend fun updateMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        status: MovieStatus,
    ): Result<Unit>

    suspend fun removeMovieStatus(groupIds: List<String>, userId: String, movieId: Int): Result<Unit>

    fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMovieStatus>>
    suspend fun getAllMarkedMovieIdsForUser(userId: String): List<Int>

    // /////////////////////////////////////////////////////////////////////////
    // Sync Lifecycle Management
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Start synchronization of user data from Firebase to Room.
     * Sets up real-time listeners for groups and users.
     * Should be called when user logs in or app starts.
     *
     * @param userId The ID of the current authenticated user
     */
    fun startSync(userId: String)

    /**
     * Stop all active synchronization listeners.
     * Should be called when user logs out or app is destroyed.
     */
    fun stopSync()

    /**
     * Observe the current synchronization state.
     * Emits updates when sync state changes (Syncing, Synced, Error, Offline).
     *
     * @return Flow of SyncState updates
     */
    fun getSyncState(): Flow<SyncState>

    /**
     * Clear all local cached data from Room.
     * Should be called on logout to prevent data leaking between user sessions.
     */
    suspend fun clearLocalData()
}
