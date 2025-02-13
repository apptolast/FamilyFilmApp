package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.remote.firebase.GroupFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class FirebaseDatabaseDatasourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val roomDatasource: RoomDatasource,
) : FirebaseDatabaseDatasource {

    val rootDatabase = database.collection(DB_ROOT_COLLECTION).document(BuildConfig.BUILD_TYPE)
    val usersCollection = rootDatabase.collection("users")
    val groupsCollection = rootDatabase.collection("groups")
    val moviesCollection = rootDatabase.collection("movies")

//    init {
//
//        // Everytime there is any change change in the database, update room
//        groupsCollection.addSnapshotListener { snapshots, e ->
//            if (e != null) {
//                Timber.e(e, "Listen failed.")
//                return@addSnapshotListener
//            }
//
//            GlobalScope.launch(Dispatchers.IO) {
//                for (docChange in snapshots!!.documentChanges) {
//                    when (docChange.type) {
//                        DocumentChange.Type.ADDED -> {
//                            Timber.d("Document added: ${docChange.document.data}")
//                            val group = docChange.document.toObject(Group::class.java)
//                            roomDatasource.insertGroup(group.toGroupTable())
//                        }
//
//                        DocumentChange.Type.MODIFIED -> {
//                            Timber.d("Document updated: ${docChange.document.data}")
//                            val group = docChange.document.toObject(Group::class.java)
//                            roomDatasource.updateGroup(group.toGroupTable())
//                        }
//
//                        DocumentChange.Type.REMOVED -> {
//                            Timber.d("Document deleted: ${docChange.document.id}")
//                            val group = docChange.document.toObject(Group::class.java)
//                            roomDatasource.deleteGroup(group.toGroupTable())
//                        }
//                    }
//                }
//            }
//        }
//    }

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    override fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit) {
        usersCollection
            .document(user.id)
            .set(user)
            .addOnSuccessListener(success)
            .addOnFailureListener(failure)
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
            "watched" to user.watched,
            "toWatch" to user.toWatch,
        )

        usersCollection.document(user.id).update(updates).addOnSuccessListener(success)
    }

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    override fun getMyGroups(userId: String): Flow<List<GroupFirebase?>> = callbackFlow {
        val listener: (QuerySnapshot?) -> Unit = { querySnapshot ->
            if (querySnapshot?.documents?.isEmpty() == false) {
                val groups = querySnapshot.documents.filterNotNull().filter {
                    val group = it.toObject(GroupFirebase::class.java)
                    group?.users?.contains(userId) == true
                }.map {
                    it.toObject(GroupFirebase::class.java)
                }.filterNotNull()

                trySend(groups)
            } else {
                Timber.d("No such document")
                trySend(emptyList())
            }
        }

        groupsCollection
            .get()
            .addOnSuccessListener(listener)

        awaitClose {}
    }

    /**
     * To add a group to the database, we needed to know the user who will create it.
     * Also we add it to the list of users of the group.
     */
    override fun createGroup(groupName: String, user: User, success: (GroupFirebase) -> Unit) {
        val uuid = UUID.randomUUID().toString()
        val groupFirebase = GroupFirebase().copy(
            id = uuid,
            ownerId = user.id,
            name = groupName,
            users = listOf(user.id),
            watchedList = emptyList(),
            toWatchList = emptyList(),
            lastUpdated = Calendar.getInstance().time,
        )
        groupsCollection
            .document(uuid)
            .set(groupFirebase)
            .addOnSuccessListener {
                Timber.d("Group created")
                success(groupFirebase)
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error creating the group")
            }
    }

    override fun updateGroup(group: Group, success: (Void?) -> Unit) {
        // Update fields
        val updates = mapOf(
            "name" to group.name,
            "users" to group.users.map{it.id},
            "watchedList" to group.watchedList,
            "toWatchList" to group.toWatchList,
            "lastUpdated" to group.lastUpdated,
        )

        groupsCollection
            .document(group.id)
            .update(updates)
            .addOnSuccessListener(success)
    }

    override fun deleteGroup(group: Group, success: (Void?) -> Unit) {
        groupsCollection
            .document(group.id)
            .delete()
            .addOnSuccessListener(success)
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

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    fun getMyGroups(userId: String): Flow<List<GroupFirebase?>>
    fun createGroup(groupName: String, user: User, success: (GroupFirebase) -> Unit)
    fun updateGroup(group: Group, success: (Void?) -> Unit)
    fun deleteGroup(group: Group, success: (Void?) -> Unit)
}
