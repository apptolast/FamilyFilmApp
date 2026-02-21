package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMovieStatus
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class FirebaseDatabaseDatasourceImpl @Inject constructor(private val database: FirebaseFirestore) :
    FirebaseDatabaseDatasource {

    val rootDatabase = database.collection(DB_ROOT_COLLECTION).document(BuildConfig.BUILD_TYPE)
    val usersCollection = rootDatabase.collection("users")
    val usernamesCollection = rootDatabase.collection("usernames")

    val groupsCollection = rootDatabase.collection("groups")
    val moviesCollection = rootDatabase.collection("movies")

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////

    override suspend fun getUsersByIds(userIds: List<String>): List<User> {
        if (userIds.isEmpty()) return emptyList()

        val users = mutableListOf<User>()

        // Firebase whereIn() supports max 10 items, so we chunk
        userIds.chunked(10).forEach { chunk ->
            try {
                val snapshot = usersCollection
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val chunkUsers = snapshot.documents.mapNotNull { document ->
                    document.toObject(User::class.java)
                }
                users.addAll(chunkUsers)
            } catch (e: Exception) {
                Timber.e(e, "Error fetching users batch: $chunk")
            }
        }

        Timber.d("Fetched ${users.size} users from Firebase out of ${userIds.size} requested")
        return users
    }

    override suspend fun createUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }

    override fun getUserById(userId: String, success: (User?) -> Unit) {
        usersCollection
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Timber.d("DocumentSnapshot data: ${document.data}")
                    success(document.toObject(User::class.java))
                } else {
                    Timber.d("No such document")
                }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception)
            }
    }

    override fun getUserByEmail(email: String, success: (User?) -> Unit) {
        usersCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.documents.isNotEmpty()) {
                    val snapshot = document.documents.first()
                    Timber.d("DocumentSnapshot data: $snapshot.data")
                    success(snapshot.toObject(User::class.java))
                } else {
                    Timber.d("No such document")
                }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception)
            }
    }

    override fun updateUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit) {
        // Update fields
        val updates = mapOf(
            "email" to user.email,
            "language" to user.language,
            "photoUrl" to user.photoUrl,
            "username" to (user.username ?: ""),
            "usernameLower" to (user.username?.lowercase() ?: ""),
        )

        usersCollection
            .document(user.id)
            .update(updates)
            .addOnSuccessListener {
                Timber.d("User updated: ${user.email}")
                success(it)
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error updating user: ${user.email}")
                failure(e)
            }
    }

    override suspend fun deleteUser(user: User) {
        usersCollection.document(user.id).delete().await()
        Timber.d("User deleted from Firestore: ${user.email}")
    }

    override suspend fun checkIfUserExists(userId: String): Boolean {
        val document = usersCollection.document(userId).get().await()
        return document.exists()
    }

    override fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val listenerRegistration = usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing user: $userId")
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    trySend(user)
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////

    override fun getMyGroups(userId: String): Flow<List<Group>> = callbackFlow {
        val listenerRegistration = groupsCollection
            .whereArrayContains("users", userId) // Assuming 'users' is an array of userIds
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting groups for user")
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val groups = snapshot.documents.mapNotNull { document ->
                        document.toObject(Group::class.java)
                    }
                    trySend(groups)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * To add a group to the database, we needed to know the user who will create it.
     * Also we add it to the list of users of the group.
     */
    override fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit) {
        val uuid = UUID.randomUUID().toString()
        val group = Group().copy(
            id = uuid,
            ownerId = user.id,
            name = groupName,
            users = listOf(user.id), // Store user IDs, not the entire User object
            lastUpdated = Calendar.getInstance().time, // Set the initial lastUpdated timestamp
        )

        groupsCollection
            .document(uuid)
            .set(group)
            .addOnSuccessListener {
                Timber.d("Group created")
                success(group)
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error creating the group")
                failure(e)
            }
    }

    override fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) {
        val updateGroup = mapOf(
            "name" to group.name,
            "ownerId" to group.ownerId,
            "users" to group.users,
            "lastUpdated" to Calendar.getInstance().time, // Set the initial lastUpdated timestamp
        )

        groupsCollection
            .document(group.id)
            .update(updateGroup)
            .addOnSuccessListener {
                Timber.d("Group updated: ${group.name}")
                success()
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error updating group: ${group.name}")
                failure(e)
            }
    }

    override fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) {
        groupsCollection.document(group.id)
            .delete()
            .addOnSuccessListener {
                Timber.d("Group deleted: ${group.name}")
                success()
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error deleting group: ${group.name}")
                failure(e)
            }
    }

    override fun addMember(group: Group, identifier: String, success: () -> Unit, failure: (Exception) -> Unit) {
        val isEmail = identifier.contains("@")

        if (isEmail) {
            resolveUserByEmail(identifier, group, success, failure)
        } else {
            resolveUserByUsername(identifier, group, success, failure)
        }
    }

    private fun resolveUserByEmail(email: String, group: Group, success: () -> Unit, failure: (Exception) -> Unit) {
        usersCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    failure(IllegalArgumentException("User with email $email not found"))
                    return@addOnSuccessListener
                }
                val user = querySnapshot.documents[0].toObject(User::class.java)
                if (user == null) {
                    failure(NullPointerException("Failed to convert document to User object"))
                    return@addOnSuccessListener
                }
                addUserToGroup(user, group, success, failure)
            }
            .addOnFailureListener { e -> failure(e) }
    }

    private fun resolveUserByUsername(
        username: String,
        group: Group,
        success: () -> Unit,
        failure: (Exception) -> Unit,
    ) {
        usernamesCollection.document(username.lowercase()).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    failure(IllegalArgumentException("User with username @$username not found"))
                    return@addOnSuccessListener
                }
                val userId = document.getString("userId")
                if (userId == null) {
                    failure(IllegalArgumentException("Invalid username record for @$username"))
                    return@addOnSuccessListener
                }
                usersCollection.document(userId).get()
                    .addOnSuccessListener { userDoc ->
                        val user = userDoc.toObject(User::class.java)
                        if (user == null) {
                            failure(IllegalArgumentException("User with username @$username not found"))
                            return@addOnSuccessListener
                        }
                        addUserToGroup(user, group, success, failure)
                    }
                    .addOnFailureListener { e -> failure(e) }
            }
            .addOnFailureListener { e -> failure(e) }
    }

    private fun addUserToGroup(user: User, group: Group, success: () -> Unit, failure: (Exception) -> Unit) {
        val updatedUsers = group.users.toMutableList()
        if (user.id !in updatedUsers) {
            updatedUsers.add(user.id)
            val updatedGroup = group.copy(
                users = updatedUsers,
                lastUpdated = Calendar.getInstance().time,
            )
            this@FirebaseDatabaseDatasourceImpl.updateGroup(
                group = updatedGroup,
                success = success,
                failure = failure,
            )
        } else {
            failure(IllegalArgumentException("User is already a member of this group"))
        }
    }

    override fun deleteMember(group: Group, user: User, success: () -> Unit, failure: (Exception) -> Unit) {
        val updatedUsers = group.users.toMutableList().apply {
            remove(user.id)
        }

        val updatedGroup = group.copy(
            users = updatedUsers,
            lastUpdated = Calendar.getInstance().time,
        )

        this@FirebaseDatabaseDatasourceImpl.updateGroup(
            group = updatedGroup,
            success = success,
            failure = failure,
        )
    }

    // /////////////////////////////////////////////////////////////////////////
    // Movie Statuses (per-group subcollection)
    // /////////////////////////////////////////////////////////////////////////

    private fun movieStatusesCollection(groupId: String) =
        groupsCollection.document(groupId).collection("movieStatuses")

    private fun movieStatusDocId(userId: String, movieId: Int) = "${userId}_$movieId"

    override suspend fun setMovieStatus(groupId: String, userId: String, movieId: Int, status: MovieStatus) {
        val docId = movieStatusDocId(userId, movieId)
        val data = mapOf(
            "userId" to userId,
            "movieId" to movieId,
            "status" to status.name,
        )
        movieStatusesCollection(groupId).document(docId).set(data).await()
        Timber.d("Movie status set: group=$groupId, user=$userId, movie=$movieId, status=$status")
    }

    override suspend fun removeMovieStatus(groupId: String, userId: String, movieId: Int) {
        val docId = movieStatusDocId(userId, movieId)
        movieStatusesCollection(groupId).document(docId).delete().await()
        Timber.d("Movie status removed: group=$groupId, user=$userId, movie=$movieId")
    }

    override fun observeMovieStatusesForGroup(groupId: String): Flow<List<GroupMovieStatus>> = callbackFlow {
        val listenerRegistration = movieStatusesCollection(groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing movie statuses for group: $groupId")
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val statuses = snapshot.documents.mapNotNull { document ->
                        try {
                            GroupMovieStatus(
                                groupId = groupId,
                                userId = document.getString("userId") ?: return@mapNotNull null,
                                movieId = (document.getLong("movieId") ?: return@mapNotNull null).toInt(),
                                status = MovieStatus.valueOf(
                                    document.getString("status") ?: return@mapNotNull null,
                                ),
                            )
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing movie status document: ${document.id}")
                            null
                        }
                    }
                    trySend(statuses)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun migrateMovieStatusesIfNeeded(userId: String, groups: List<Group>) {
        val userDoc = usersCollection.document(userId).get().await()
        if (!userDoc.exists()) return

        // Check migration guard
        val alreadyMigrated = userDoc.getBoolean("movieStatusMigrated") ?: false
        if (alreadyMigrated) {
            Timber.d("Movie status migration already completed for user: $userId")
            return
        }

        // Extract statusMovies from raw document data
        val statusMoviesRaw = userDoc.get("statusMovies") as? Map<String, String> ?: emptyMap()
        if (statusMoviesRaw.isEmpty()) {
            // No movies to migrate, mark as migrated
            usersCollection.document(userId)
                .update("movieStatusMigrated", true)
                .await()
            Timber.d("No movie statuses to migrate for user: $userId")
            return
        }

        if (groups.isEmpty()) {
            // No groups to assign movies to, mark as migrated
            usersCollection.document(userId)
                .update("movieStatusMigrated", true)
                .await()
            Timber.d("No groups for user, skipping migration: $userId")
            return
        }

        Timber.d("Migrating ${statusMoviesRaw.size} movie statuses to ${groups.size} groups for user: $userId")

        // Build all write operations: movieId × status × groupId
        data class MigrationEntry(val groupId: String, val movieId: String, val status: String)
        val entries = mutableListOf<MigrationEntry>()
        for ((movieId, status) in statusMoviesRaw) {
            for (group in groups) {
                entries.add(MigrationEntry(group.id, movieId, status))
            }
        }

        // Firestore batches limited to 500 ops. Use 450-op chunks to leave room for guard update
        val chunks = entries.chunked(450)
        for ((index, chunk) in chunks.withIndex()) {
            val batch = database.batch()

            for (entry in chunk) {
                val docId = movieStatusDocId(userId, entry.movieId.toIntOrNull() ?: continue)
                val docRef = movieStatusesCollection(entry.groupId).document(docId)
                batch.set(
                    docRef,
                    mapOf(
                        "userId" to userId,
                        "movieId" to (entry.movieId.toIntOrNull() ?: continue),
                        "status" to entry.status,
                    ),
                )
            }

            // On the last chunk, set the migration guard
            if (index == chunks.lastIndex) {
                batch.update(usersCollection.document(userId), "movieStatusMigrated", true)
            }

            batch.commit().await()
            Timber.d("Migration batch ${index + 1}/${chunks.size} committed")
        }

        Timber.d("Movie status migration completed for user: $userId")
    }

    // /////////////////////////////////////////////////////////////////////////
    // Usernames (uniqueness collection)
    // /////////////////////////////////////////////////////////////////////////

    override suspend fun claimUsername(username: String, userId: String): Boolean {
        val usernameLower = username.lowercase()
        val docRef = usernamesCollection.document(usernameLower)
        return try {
            database.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    val existingUserId = snapshot.getString("userId")
                    if (existingUserId == userId) {
                        // Already claimed by this user (reclaim after partial failure)
                        return@runTransaction
                    }
                    throw Exception("Username already taken")
                }
                transaction.set(docRef, mapOf("userId" to userId))
            }.await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to claim username: $username")
            false
        }
    }

    override suspend fun releaseUsername(username: String) {
        val usernameLower = username.lowercase()
        try {
            usernamesCollection.document(usernameLower).delete().await()
            Timber.d("Released username: $username")
        } catch (e: Exception) {
            Timber.e(e, "Error releasing username: $username")
        }
    }

    override suspend fun isUsernameAvailable(username: String): Boolean {
        val usernameLower = username.lowercase()
        val doc = usernamesCollection.document(usernameLower).get().await()
        return !doc.exists()
    }

    companion object {
        const val DB_ROOT_COLLECTION = "FFA"
    }
}

interface FirebaseDatabaseDatasource {
    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    suspend fun createUser(user: User)
    fun getUserById(userId: String, success: (User?) -> Unit)
    suspend fun getUsersByIds(userIds: List<String>): List<User>
    fun getUserByEmail(email: String, success: (User?) -> Unit)
    fun updateUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit)
    suspend fun deleteUser(user: User)
    suspend fun checkIfUserExists(userId: String): Boolean
    fun observeUser(userId: String): Flow<User?>
    suspend fun claimUsername(username: String, userId: String): Boolean
    suspend fun releaseUsername(username: String)
    suspend fun isUsernameAvailable(username: String): Boolean

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    fun getMyGroups(userId: String): Flow<List<Group>>
    fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit)
    fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun addMember(group: Group, identifier: String, success: () -> Unit, failure: (Exception) -> Unit)
    fun deleteMember(group: Group, user: User, success: () -> Unit, failure: (Exception) -> Unit)

    // /////////////////////////////////////////////////////////////////////////
    // Movie Statuses (per-group)
    // /////////////////////////////////////////////////////////////////////////
    suspend fun setMovieStatus(groupId: String, userId: String, movieId: Int, status: MovieStatus)
    suspend fun removeMovieStatus(groupId: String, userId: String, movieId: Int)
    fun observeMovieStatusesForGroup(groupId: String): Flow<List<GroupMovieStatus>>
    suspend fun migrateMovieStatusesIfNeeded(userId: String, groups: List<Group>)
}
