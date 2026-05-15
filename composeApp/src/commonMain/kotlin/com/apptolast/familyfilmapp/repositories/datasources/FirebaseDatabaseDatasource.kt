@file:OptIn(ExperimentalUuidApi::class)

package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMediaStatus
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FieldPath
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Layout: FFA/{BUILD_TYPE}/{users|usernames|groups|movies} plus groups/{id}/movieStatuses subcollection.
class FirebaseDatabaseDatasourceImpl(private val crashReporter: CrashReporter) : FirebaseDatabaseDatasource {

    private val database: FirebaseFirestore = Firebase.firestore

    private val rootDatabase get() = database.collection(DB_ROOT_COLLECTION).document(BuildConfig.BUILD_TYPE)
    private val usersCollection get() = rootDatabase.collection("users")
    private val usernamesCollection get() = rootDatabase.collection("usernames")
    private val groupsCollection get() = rootDatabase.collection("groups")

    // ─── Users ──────────────────────────────────────────────────────────────

    override suspend fun createUser(user: User) {
        require(user.id.isNotBlank()) { "Cannot create user with blank ID" }
        usersCollection.document(user.id).set(user.toFirestoreDto())
    }

    override suspend fun getUserById(userId: String): User? = try {
        val doc = usersCollection.document(userId).get()
        if (doc.exists) doc.data<UserFirestoreDto>().toDomain() else null
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        null
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<User> {
        if (userIds.isEmpty()) return emptyList()
        val users = mutableListOf<User>()
        // Firestore whereIn supports max 10 items; chunk and merge.
        userIds.chunked(10).forEach { chunk ->
            try {
                val snapshot = usersCollection
                    .where(path = FieldPath.documentId, inArray = chunk)
                    .get()
                users += snapshot.documents.mapNotNull { it.toUserDomainOrNull() }
            } catch (e: Throwable) {
                crashReporter.recordException(e)
            }
        }
        return users
    }

    override suspend fun getUserByEmail(email: String): User? = try {
        val snapshot = usersCollection.where { "email" equalTo email }.get()
        snapshot.documents.firstOrNull()?.toUserDomainOrNull()
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        null
    }

    override suspend fun updateUser(user: User) {
        require(user.id.isNotBlank()) { "Cannot update user with blank ID" }
        usersCollection.document(user.id).update(
            "email" to user.email,
            "language" to user.language,
            "photoUrl" to user.photoUrl,
            "username" to user.username.orEmpty(),
            "usernameLower" to user.username?.lowercase().orEmpty(),
            "hasRemovedAds" to user.hasRemovedAds,
        )
    }

    override suspend fun deleteUser(user: User) {
        require(user.id.isNotBlank()) { "Cannot delete user with blank ID" }
        usersCollection.document(user.id).delete()
    }

    override suspend fun checkIfUserExists(userId: String): Boolean = usersCollection.document(userId).get().exists

    override fun observeUser(userId: String): Flow<User?> = usersCollection.document(userId).snapshots.map { snap ->
        if (snap.exists) snap.data<UserFirestoreDto>().toDomain() else null
    }

    override suspend fun claimUsername(username: String, userId: String): Boolean {
        val usernameLower = username.lowercase()
        val docRef = usernamesCollection.document(usernameLower)
        return try {
            // GitLive's runTransaction lambda has Transaction as receiver, not as parameter.
            database.runTransaction {
                val snap = get(docRef)
                if (snap.exists) {
                    val existing = snap.optional<String>("userId")
                    if (existing == userId) return@runTransaction
                    error("Username already taken")
                }
                set(docRef, UsernameClaimDto(userId = userId))
            }
            true
        } catch (e: Throwable) {
            crashReporter.recordException(e)
            false
        }
    }

    override suspend fun releaseUsername(username: String) {
        try {
            usernamesCollection.document(username.lowercase()).delete()
        } catch (e: Throwable) {
            crashReporter.recordException(e)
        }
    }

    override suspend fun isUsernameAvailable(username: String): Boolean =
        !usernamesCollection.document(username.lowercase()).get().exists

    override suspend fun updateHasRemovedAds(userId: String, hasRemovedAds: Boolean) {
        usersCollection.document(userId).update("hasRemovedAds" to hasRemovedAds)
    }

    // ─── Groups ─────────────────────────────────────────────────────────────

    override fun getMyGroups(userId: String): Flow<List<Group>> =
        // GitLive names the array-contains operator `contains` (not `arrayContains`).
        groupsCollection.where { "users" contains userId }.snapshots.map { snap ->
            snap.documents.mapNotNull { it.toGroupDomainOrNull() }
        }

    override suspend fun createGroup(groupName: String, user: User): Group {
        val id = Uuid.random().toString()
        val nowMillis = Clock.System.now().toEpochMilliseconds()
        val dto = GroupFirestoreDto(
            id = id,
            ownerId = user.id,
            name = groupName,
            users = listOf(user.id),
            lastUpdated = nowMillis,
        )
        groupsCollection.document(id).set(dto)
        return dto.toDomain()
    }

    override suspend fun updateGroup(group: Group) {
        groupsCollection.document(group.id).update(
            "name" to group.name,
            "ownerId" to group.ownerId,
            "users" to group.users,
            "lastUpdated" to Clock.System.now().toEpochMilliseconds(),
        )
    }

    override suspend fun deleteGroup(group: Group) {
        groupsCollection.document(group.id).delete()
    }

    override suspend fun addMember(group: Group, identifier: String) {
        val isEmail = identifier.contains("@")
        val resolvedUser = if (isEmail) {
            getUserByEmail(identifier) ?: error("User with email $identifier not found")
        } else {
            val claim = usernamesCollection.document(identifier.lowercase()).get()
            if (!claim.exists) error("User with username @$identifier not found")
            val targetUserId = claim.optional<String>("userId")
                ?: error("Invalid username record for @$identifier")
            getUserById(targetUserId) ?: error("User with username @$identifier not found")
        }

        if (resolvedUser.id in group.users) {
            error("User is already a member of this group")
        }
        updateGroup(group.copy(users = group.users + resolvedUser.id))
    }

    override suspend fun deleteMember(group: Group, user: User) {
        updateGroup(group.copy(users = group.users.filterNot { it == user.id }))
    }

    // ─── Movie statuses (per-group subcollection) ──────────────────────────

    private fun movieStatusesCollection(groupId: String) =
        groupsCollection.document(groupId).collection("movieStatuses")

    private fun movieStatusDocId(userId: String, movieId: Int) = "${userId}_$movieId"

    override suspend fun setMovieStatus(
        groupId: String,
        userId: String,
        movieId: Int,
        status: MediaStatus,
        mediaType: MediaType,
    ) {
        val docId = movieStatusDocId(userId, movieId)
        val dto = GroupMovieStatusFirestoreDto(
            userId = userId,
            movieId = movieId,
            status = status.name,
            mediaType = mediaType.name,
        )
        movieStatusesCollection(groupId).document(docId).set(dto)
    }

    override suspend fun removeMovieStatus(groupId: String, userId: String, movieId: Int) {
        val docId = movieStatusDocId(userId, movieId)
        movieStatusesCollection(groupId).document(docId).delete()
    }

    override fun observeMovieStatusesForGroup(groupId: String): Flow<List<GroupMediaStatus>> =
        movieStatusesCollection(groupId).snapshots.map { snap ->
            snap.documents.mapNotNull { it.toGroupMediaStatusOrNull(groupId) }
        }

    // One-shot migration: users/{uid}.statusMovies → groups/{gid}/movieStatuses/{uid_movieId}.
    // Guarded by `movieStatusMigrated` on the user doc so it runs at most once per install.
    override suspend fun migrateMovieStatusesIfNeeded(userId: String, groups: List<Group>) {
        val userDoc = usersCollection.document(userId).get()
        if (!userDoc.exists) return
        val alreadyMigrated = userDoc.optional<Boolean>("movieStatusMigrated") == true
        if (alreadyMigrated) return

        val statusMoviesRaw: Map<String, String> =
            userDoc.optional<Map<String, String>>("statusMovies") ?: emptyMap()

        if (statusMoviesRaw.isEmpty() || groups.isEmpty()) {
            usersCollection.document(userId).update("movieStatusMigrated" to true)
            return
        }

        data class MigrationEntry(val groupId: String, val movieId: Int, val status: String)
        val entries = buildList {
            for ((rawMovieId, status) in statusMoviesRaw) {
                val movieId = rawMovieId.toIntOrNull() ?: continue
                for (group in groups) {
                    add(MigrationEntry(group.id, movieId, status))
                }
            }
        }

        // 450-op chunks leave room for the migration guard write on the last chunk.
        val chunks = entries.chunked(450)
        chunks.forEachIndexed { index, chunk ->
            val batch = database.batch()
            for (entry in chunk) {
                val docId = movieStatusDocId(userId, entry.movieId)
                val docRef = movieStatusesCollection(entry.groupId).document(docId)
                batch.set(
                    documentRef = docRef,
                    data = GroupMovieStatusFirestoreDto(
                        userId = userId,
                        movieId = entry.movieId,
                        status = entry.status,
                        mediaType = MediaType.MOVIE.name,
                    ),
                )
            }
            if (index == chunks.lastIndex) {
                batch.update(usersCollection.document(userId), "movieStatusMigrated" to true)
            }
            batch.commit()
        }
    }

    // ─── Mapping helpers ───────────────────────────────────────────────────

    private fun DocumentSnapshot.toUserDomainOrNull(): User? = try {
        if (exists) data<UserFirestoreDto>().toDomain() else null
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        null
    }

    private fun DocumentSnapshot.toGroupDomainOrNull(): Group? = try {
        if (exists) data<GroupFirestoreDto>().toDomain() else null
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        null
    }

    private fun DocumentSnapshot.toGroupMediaStatusOrNull(groupId: String): GroupMediaStatus? = try {
        val userId = optional<String>("userId") ?: return null
        val movieId = optional<Long>("movieId")?.toInt() ?: return null
        val statusStr = optional<String>("status") ?: return null
        val status = runCatching { MediaStatus.valueOf(statusStr) }.getOrNull() ?: return null
        val mediaTypeStr = optional<String>("mediaType") ?: MediaType.MOVIE.name
        val mediaType = runCatching { MediaType.valueOf(mediaTypeStr) }.getOrDefault(MediaType.MOVIE)
        GroupMediaStatus(
            groupId = groupId,
            userId = userId,
            mediaId = movieId,
            status = status,
            mediaType = mediaType,
        )
    } catch (e: Throwable) {
        crashReporter.recordException(e)
        null
    }

    private inline fun <reified T> DocumentSnapshot.optional(field: String): T? =
        if (contains(field)) get<T>(field) else null

    companion object {
        const val DB_ROOT_COLLECTION = "FFA"
    }
}

interface FirebaseDatabaseDatasource {
    // Users
    suspend fun createUser(user: User)
    suspend fun getUserById(userId: String): User?
    suspend fun getUsersByIds(userIds: List<String>): List<User>
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun checkIfUserExists(userId: String): Boolean
    fun observeUser(userId: String): Flow<User?>
    suspend fun claimUsername(username: String, userId: String): Boolean
    suspend fun releaseUsername(username: String)
    suspend fun isUsernameAvailable(username: String): Boolean
    suspend fun updateHasRemovedAds(userId: String, hasRemovedAds: Boolean)

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    suspend fun createGroup(groupName: String, user: User): Group
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    suspend fun addMember(group: Group, identifier: String)
    suspend fun deleteMember(group: Group, user: User)

    // Movie statuses (per-group subcollection)
    suspend fun setMovieStatus(
        groupId: String,
        userId: String,
        movieId: Int,
        status: MediaStatus,
        mediaType: MediaType = MediaType.MOVIE,
    )
    suspend fun removeMovieStatus(groupId: String, userId: String, movieId: Int)
    fun observeMovieStatusesForGroup(groupId: String): Flow<List<GroupMediaStatus>>
    suspend fun migrateMovieStatusesIfNeeded(userId: String, groups: List<Group>)
}

// ─── Firestore DTOs ────────────────────────────────────────────────────────

@Serializable
private data class UserFirestoreDto(
    val id: String = "",
    val email: String = "",
    val language: String = "",
    val photoUrl: String = "",
    val username: String = "",
    val usernameLower: String = "",
    val hasRemovedAds: Boolean = false,
)

private fun UserFirestoreDto.toDomain(): User = User(
    id = id,
    email = email,
    language = language,
    photoUrl = photoUrl,
    username = username.takeIf { it.isNotBlank() },
    hasRemovedAds = hasRemovedAds,
)

private fun User.toFirestoreDto(): UserFirestoreDto = UserFirestoreDto(
    id = id,
    email = email,
    language = language,
    photoUrl = photoUrl,
    username = username.orEmpty(),
    usernameLower = username?.lowercase().orEmpty(),
    hasRemovedAds = hasRemovedAds,
)

@Serializable
private data class GroupFirestoreDto(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val users: List<String> = emptyList(),
    val lastUpdated: Long? = null,
)

private fun GroupFirestoreDto.toDomain(): Group = Group(
    id = id,
    ownerId = ownerId,
    name = name,
    users = users,
    lastUpdated = lastUpdated?.let { kotlin.time.Instant.fromEpochMilliseconds(it) },
)

@Serializable
private data class GroupMovieStatusFirestoreDto(
    val userId: String = "",
    val movieId: Int = 0,
    val status: String = "",
    val mediaType: String = "MOVIE",
)

@Serializable
private data class UsernameClaimDto(val userId: String = "")
