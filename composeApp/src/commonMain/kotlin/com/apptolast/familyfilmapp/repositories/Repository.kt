package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMediaStatus
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toGroupMediaStatus
import com.apptolast.familyfilmapp.model.room.toGroupMediaStatusTable
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface Repository {
    // Media (Movies + TV Shows)
    suspend fun getPopularMoviesList(page: Int = 1): Result<List<Media>>
    suspend fun searchTmdbMovieByName(string: String): Result<List<Media>>
    suspend fun getMoviesByIds(ids: List<Int>): Result<List<Media>>
    suspend fun getPopularTvShowsList(page: Int = 1): Result<List<Media>>
    suspend fun searchMulti(query: String): Result<List<Media>>
    suspend fun getTvShowsByIds(ids: List<Int>): Result<List<Media>>

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    suspend fun createGroup(groupName: String, userId: String): Result<Group>
    suspend fun updateGroup(group: Group): Result<Unit>
    suspend fun deleteGroup(groupId: String): Result<Unit>
    suspend fun addMember(groupId: String, identifier: String): Result<Unit>
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
    suspend fun updateHasRemovedAds(userId: String, hasRemovedAds: Boolean): Result<Unit>

    // Media Statuses (per-group)
    suspend fun updateMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        status: MediaStatus,
        mediaType: MediaType = MediaType.MOVIE,
    ): Result<Unit>

    suspend fun removeMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        mediaType: MediaType = MediaType.MOVIE,
    ): Result<Unit>

    fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMediaStatus>>
    suspend fun getAllMarkedMovieIdsForUser(userId: String): List<Int>

    // Sync lifecycle
    fun startSync(userId: String)
    fun stopSync()
    fun getSyncState(): Flow<SyncState>
    suspend fun clearLocalData()
}

// Room is the UI's single source of truth; Firestore mirrors into Room via startSync listeners.
// Write-through: mutations write to Firestore first (suspending), then Room, before returning.
class RepositoryImpl(
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
    private val coroutineScope: CoroutineScope,
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val crashReporter: CrashReporter,
) : Repository {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Synced)
    private var syncJob: Job? = null

    // ── Media ──────────────────────────────────────────────────────────────

    override suspend fun getPopularMoviesList(page: Int): Result<List<Media>> = runCatching {
        tmdbDatasource.getPopularMovies(page)
            .map { it.toDomain(tmdbLocaleManager.countryCode) }
            .filterAdultContent()
    }

    override suspend fun searchTmdbMovieByName(string: String): Result<List<Media>> = runCatching {
        tmdbDatasource.searchMovieByName(string)
            .map { it.toDomain(tmdbLocaleManager.countryCode) }
            .filterAdultContent()
    }

    override suspend fun getMoviesByIds(ids: List<Int>): Result<List<Media>> = runCatching {
        ids.map { tmdbDatasource.searchMovieById(it).toDomain(tmdbLocaleManager.countryCode) }
            .filterAdultContent()
    }

    override suspend fun getPopularTvShowsList(page: Int): Result<List<Media>> = runCatching {
        tmdbDatasource.getPopularTvShows(page)
            .map { it.toDomain(tmdbLocaleManager.countryCode) }
            .filterAdultContent()
    }

    override suspend fun searchMulti(query: String): Result<List<Media>> = runCatching {
        tmdbDatasource.searchMulti(query)
            .mapNotNull { it.toDomain() }
            .filterAdultContent()
    }

    override suspend fun getTvShowsByIds(ids: List<Int>): Result<List<Media>> = runCatching {
        ids.map { tmdbDatasource.getTvShowById(it).toDomain(tmdbLocaleManager.countryCode) }
            .filterAdultContent()
    }

    private fun List<Media>.filterAdultContent(): List<Media> {
        if (tmdbLocaleManager.includeAdult.value) return this
        return filterNot { it.adult }
    }

    // ── Groups ─────────────────────────────────────────────────────────────

    override fun getMyGroups(userId: String): Flow<List<Group>> =
        roomDatasource.getMyGroups(userId).map { tables -> tables.map { it.toGroup() } }

    override suspend fun createGroup(groupName: String, userId: String): Result<Group> = runCatching {
        val user = roomDatasource.getUser(userId).first()?.toUser()
            ?: error("User $userId not found in local cache")
        val group = firebaseDatabaseDatasource.createGroup(groupName, user)
        roomDatasource.insertGroup(group.toGroupTable())
        group
    }

    override suspend fun updateGroup(group: Group): Result<Unit> = runCatching {
        firebaseDatabaseDatasource.updateGroup(group)
        roomDatasource.insertGroup(group.toGroupTable())
    }

    override suspend fun deleteGroup(groupId: String): Result<Unit> = runCatching {
        val group = roomDatasource.getGroupById(groupId).first()?.toGroup()
            ?: error("Group $groupId not found in local cache")
        firebaseDatabaseDatasource.deleteGroup(group)
        roomDatasource.deleteGroup(group.toGroupTable())
    }

    override suspend fun addMember(groupId: String, identifier: String): Result<Unit> = runCatching {
        val group = roomDatasource.getGroupById(groupId).first()?.toGroup()
            ?: error("Group $groupId not found in local cache")
        firebaseDatabaseDatasource.addMember(group, identifier)
        val resolved = if (identifier.contains("@")) {
            roomDatasource.getUserByEmail(identifier).first()
        } else {
            roomDatasource.getUserByUsername(identifier)
        }
        if (resolved != null) {
            roomDatasource.insertGroup(group.copy(users = group.users + resolved.toUser().id).toGroupTable())
        }
    }

    override suspend fun removeMember(groupId: String, userId: String): Result<Unit> = runCatching {
        val group = roomDatasource.getGroupById(groupId).first()?.toGroup()
            ?: error("Group $groupId not found in local cache")
        val user = roomDatasource.getUser(userId).first()?.toUser()
            ?: error("User $userId not found in local cache")
        firebaseDatabaseDatasource.deleteMember(group, user)
        roomDatasource.insertGroup(group.copy(users = group.users - user.id).toGroupTable())
    }

    // ── Users ──────────────────────────────────────────────────────────────

    override suspend fun createUser(user: User): Result<Unit> = runCatching {
        firebaseDatabaseDatasource.createUser(user)
    }

    override fun getUserById(userId: String): Flow<User> =
        roomDatasource.getUser(userId).filterNotNull().map { it.toUser() }

    override suspend fun updateUser(user: User): Result<Unit> = runCatching {
        require(user.id.isNotBlank()) { "Cannot update user with blank ID" }
        firebaseDatabaseDatasource.updateUser(user)
        roomDatasource.insertUser(user.toUserTable())
    }

    override suspend fun deleteUser(user: User): Result<Unit> = runCatching {
        firebaseDatabaseDatasource.deleteUser(user)
    }

    override suspend fun checkIfUserExists(userId: String): Boolean = try {
        firebaseDatabaseDatasource.checkIfUserExists(userId)
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        false
    }

    override suspend fun isUsernameAvailable(username: String): Boolean = try {
        firebaseDatabaseDatasource.isUsernameAvailable(username)
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        false // Fail closed: treat as unavailable on error
    }

    override suspend fun updateHasRemovedAds(userId: String, hasRemovedAds: Boolean): Result<Unit> = runCatching {
        firebaseDatabaseDatasource.updateHasRemovedAds(userId, hasRemovedAds)
        val userTable = roomDatasource.getUser(userId).first()
        if (userTable != null) {
            roomDatasource.insertUser(userTable.copy(hasRemovedAds = hasRemovedAds))
        }
    }

    override suspend fun updateUsername(user: User, newUsername: String): Result<Unit> = runCatching {
        val oldUsername = user.username
        val claimed = firebaseDatabaseDatasource.claimUsername(newUsername, user.id)
        if (!claimed) error("Username '$newUsername' is already taken")

        val updatedUser = user.copy(username = newUsername)
        firebaseDatabaseDatasource.updateUser(updatedUser)

        if (!oldUsername.isNullOrBlank()) {
            firebaseDatabaseDatasource.releaseUsername(oldUsername)
        }
        roomDatasource.insertUser(updatedUser.toUserTable())
    }

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> = runCatching {
        if (userIds.isEmpty()) return@runCatching emptyList()
        // Try Room first (local cache)
        val roomUsers = roomDatasource.getUsersByIds(userIds).map { it.toUser() }
        if (roomUsers.size == userIds.size) return@runCatching roomUsers

        // Fetch missing IDs from Firestore and persist
        val foundIds = roomUsers.map { it.id }.toSet()
        val missingIds = userIds.filter { it !in foundIds }
        val firebaseUsers = if (missingIds.isNotEmpty()) {
            firebaseDatabaseDatasource.getUsersByIds(missingIds).also { remote ->
                remote.forEach { roomDatasource.insertUser(it.toUserTable()) }
            }
        } else {
            emptyList()
        }
        roomUsers + firebaseUsers
    }

    // ── Movie statuses ─────────────────────────────────────────────────────

    override suspend fun updateMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        status: MediaStatus,
        mediaType: MediaType,
    ): Result<Unit> = runCatching {
        for (groupId in groupIds) {
            firebaseDatabaseDatasource.setMovieStatus(groupId, userId, movieId, status, mediaType)
            roomDatasource.insertMovieStatus(
                GroupMediaStatus(groupId, userId, movieId, status, mediaType).toGroupMediaStatusTable(),
            )
        }
    }

    override suspend fun removeMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        mediaType: MediaType,
    ): Result<Unit> = runCatching {
        for (groupId in groupIds) {
            firebaseDatabaseDatasource.removeMovieStatus(groupId, userId, movieId)
            roomDatasource.deleteMovieStatus(groupId, userId, movieId, mediaType.name)
        }
    }

    override fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMediaStatus>> =
        roomDatasource.getMovieStatusesByGroup(groupId).map { tables ->
            tables.map { it.toGroupMediaStatus() }
        }

    override suspend fun getAllMarkedMovieIdsForUser(userId: String): List<Int> =
        roomDatasource.getAllMovieIdsForUser(userId)

    // ── Sync lifecycle ─────────────────────────────────────────────────────

    override fun startSync(userId: String) {
        syncJob?.cancel()
        syncJob = coroutineScope.launch {
            try {
                _syncState.value = SyncState.Syncing

                // Pull and cache the authenticated user
                firebaseDatabaseDatasource.getUserById(userId)?.let { authenticatedUser ->
                    try {
                        roomDatasource.insertUser(authenticatedUser.toUserTable())
                        tmdbLocaleManager.update(authenticatedUser.language)
                    } catch (e: Throwable) {
                        crashReporter.recordException(e)
                    }
                }

                // Subscribe to Firestore groups for this user
                firebaseDatabaseDatasource.getMyGroups(userId).collect { remoteGroups ->
                    val remoteGroupIds = remoteGroups.map { it.id }.toSet()

                    // Differential sync: drop local groups missing remotely
                    try {
                        val localGroups = roomDatasource.getMyGroups(userId).first()
                        localGroups.forEach { localGroup ->
                            if (localGroup.groupId !in remoteGroupIds) {
                                roomDatasource.deleteGroup(localGroup)
                            }
                        }
                    } catch (e: Throwable) {
                        crashReporter.recordException(e)
                    }

                    // Upsert remote groups into Room
                    remoteGroups.forEach { group ->
                        try {
                            roomDatasource.insertGroup(group.toGroupTable())
                        } catch (e: Throwable) {
                            crashReporter.recordException(e)
                        }
                    }

                    // One-time migration of statusMovies → per-group docs.
                    try {
                        firebaseDatabaseDatasource.migrateMovieStatusesIfNeeded(userId, remoteGroups)
                    } catch (e: Throwable) {
                        crashReporter.recordException(e)
                    }

                    // Mirror per-group movie statuses into Room
                    remoteGroups.forEach { group ->
                        try {
                            syncMovieStatusesForGroup(group.id)
                        } catch (e: Throwable) {
                            crashReporter.recordException(e)
                        }
                    }
                    _syncState.value = SyncState.Synced
                }
            } catch (e: Throwable) {
                crashReporter.recordException(e)
                _syncState.value = SyncState.Error(
                    message = e.message ?: "Unknown sync error",
                    throwable = e,
                )
            }
        }
    }

    private suspend fun syncMovieStatusesForGroup(groupId: String) {
        val remoteStatuses = firebaseDatabaseDatasource.observeMovieStatusesForGroup(groupId).first()
        roomDatasource.deleteMovieStatusesByGroup(groupId)
        if (remoteStatuses.isNotEmpty()) {
            roomDatasource.insertAllMovieStatuses(remoteStatuses.map { it.toGroupMediaStatusTable() })
        }
    }

    override fun stopSync() {
        syncJob?.cancel()
        syncJob = null
        _syncState.value = SyncState.Synced
    }

    override suspend fun clearLocalData() {
        roomDatasource.clearAllData()
    }

    override fun getSyncState(): Flow<SyncState> = _syncState.asStateFlow()
}
