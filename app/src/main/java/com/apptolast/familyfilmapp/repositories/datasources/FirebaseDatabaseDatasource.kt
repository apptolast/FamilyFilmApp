package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(DelicateCoroutinesApi::class)
class FirebaseDatabaseDatasourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val roomDatasource: RoomDatasource,
    private val coroutineScope: CoroutineScope, // Inject CoroutineScope
) : FirebaseDatabaseDatasource {

    val rootDatabase = database.collection(DB_ROOT_COLLECTION).document(BuildConfig.BUILD_TYPE)
    val usersCollection = rootDatabase.collection("users")
    val groupsCollection = rootDatabase.collection("groups")
    val moviesCollection = rootDatabase.collection("movies")

    init {
        setupUserChangeListener()
        setupGroupChangeListener()
    }

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    private fun setupUserChangeListener() {
        usersCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Timber.e(e, "Listen failed.")
                return@addSnapshotListener
            }

            coroutineScope.launch(Dispatchers.IO) {
                // Use injected CoroutineScope
                for (docChange in snapshots!!.documentChanges) {
                    val user = docChange.document.toObject(User::class.java)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            Timber.d("User added: ${user.email}")
                            roomDatasource.insertUser(user.toUserTable()) // Assume that insertGroup does a REPLACE
                        }

                        DocumentChange.Type.MODIFIED -> {
                            Timber.d("User updated: ${user.email}")
                            roomDatasource.updateUser(user.toUserTable()) // Assume that insertGroup does a REPLACE
                        }

                        DocumentChange.Type.REMOVED -> {
                            Timber.d("User deleted: ${user.email}")
                            roomDatasource.deleteUser(user.toUserTable())
                        }
                    }
                }
            }
        }
    }

    override fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit) {
        usersCollection
            .document(user.id)
            .set(user)
            .addOnSuccessListener(success)
            .addOnFailureListener(failure)
    }

    fun checkNewUser() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val isNewUser = document.getBoolean("isNewUser") ?: false
                        if (isNewUser) {
                            println("El usuario es nuevo (Firestore).")
                            // Realizar acciones para usuarios nuevos
                            userRef.update("isNewUser", false) // Marcar como no nuevo
                        } else {
                            println("El usuario ya existe (Firestore).")
                            // Realizar acciones para usuarios existentes
                        }
                    } else {
                        println("El usuario es nuevo (Firestore - primera vez).")
                        // El usuario no existe en la base de datos, es nuevo
                        val newUser = hashMapOf(
                            "isNewUser" to true
                        )
                        userRef.set(newUser)
                        // Realizar acciones para usuarios nuevos
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al comprobar el usuario: $exception")
                }
        }
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

    override fun updateUser(user: User, success: (Void?) -> Unit) {
        // Update fields
        val updates = mapOf(
            "email" to user.email,
            "language" to user.language,
            "statusMovies" to user.statusMovies,
        )

        usersCollection
            .document(user.id)
            .update(updates)
            .addOnSuccessListener(success)
            .addOnFailureListener {
                Timber.e(it, "Error updating user in firestore")
            }
    }

    override fun deleteUser(user: User, success: () -> Unit, failure: (Exception) -> Unit) {
        usersCollection.document(user.id)
            .delete()
            .addOnSuccessListener {
                Timber.d("User deleted from Firestore: ${user.email}")
                success()
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error deleting user from Firestore: ${user.email}")
                failure(e)
            }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    private fun setupGroupChangeListener() {
        groupsCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Timber.e(e, "Listen failed.")
                return@addSnapshotListener
            }

            coroutineScope.launch(Dispatchers.IO) {
                // Use injected CoroutineScope
                for (docChange in snapshots!!.documentChanges) {
                    val group = docChange.document.toObject(Group::class.java)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            Timber.d("Group added: ${group.name}")
                            roomDatasource.insertGroup(group.toGroupTable()) // Assume that insertGroup does a REPLACE
                        }

                        DocumentChange.Type.MODIFIED -> {
                            Timber.d("Group updated: ${group.name}")
                            roomDatasource.updateGroup(group.toGroupTable()) // Assume that insertGroup does a REPLACE
                        }

                        DocumentChange.Type.REMOVED -> {
                            Timber.d("Group deleted: ${group.name}")
                            roomDatasource.deleteGroup(group.toGroupTable())
                        }
                    }
                }
            }
        }
    }

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

    override fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit) {
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

                val updatedUsers = group.users.toMutableList()
                // If the email is not found in the group's user list, add it
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
                    failure(IllegalArgumentException("User with email $email is already a member of this group"))
                }
            }
            .addOnFailureListener { e ->
                failure(e)
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

    companion object {
        const val DB_ROOT_COLLECTION = "FFA"
    }
}

interface FirebaseDatabaseDatasource {
    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit)
    fun getUserById(userId: String, success: (User?) -> Unit)
    fun getUserByEmail(email: String, success: (User?) -> Unit)
    fun updateUser(user: User, success: (Void?) -> Unit)
    fun deleteUser(user: User, success: () -> Unit, failure: (Exception) -> Unit)

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    fun getMyGroups(userId: String): Flow<List<Group>>
    fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit)
    fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit)
    fun deleteMember(group: Group, user: User, success: () -> Unit, failure: (Exception) -> Unit)
}
