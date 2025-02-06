package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
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

    ///////////////////////////////////////////////////////////////////////////
    // Users
    ///////////////////////////////////////////////////////////////////////////
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

    ///////////////////////////////////////////////////////////////////////////
    // Groups
    ///////////////////////////////////////////////////////////////////////////
    /**
     * To add a group to the database, we needed to know the user who will create it.
     * Also we add it to the list of users of the group.
     */
    override fun createGroup(groupName: String, user: User, success: (Group) -> Unit) {
        val uuid = UUID.randomUUID().toString()
        val group = Group().copy(
            id = uuid,
            ownerId = user.id,
            name = groupName,
            users = listOf(user),
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
            }
    }

    override fun updateGroup(group: Group, success: (Void?) -> Unit) {
        groupsCollection
            .document(group.id)
            .update(mapOf("name" to group.name))
            .addOnSuccessListener(success)
    }

    companion object {
        const val DB_ROOT_COLLECTION = "FFA"
    }
}

interface FirebaseDatabaseDatasource {
    ///////////////////////////////////////////////////////////////////////////
    // Users
    ///////////////////////////////////////////////////////////////////////////
    fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit)
    fun getUserById(userId: String, success: (User?) -> Unit)

    ///////////////////////////////////////////////////////////////////////////
    // Groups
    ///////////////////////////////////////////////////////////////////////////
    fun createGroup(groupName: String, user: User, success: (Group) -> Unit)
    fun updateGroup(group: Group, success: (Void?) -> Unit)
}
