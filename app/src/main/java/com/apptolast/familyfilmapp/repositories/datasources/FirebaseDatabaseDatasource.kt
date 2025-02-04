package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    init {

        // Everytime there is any change change in the database, update room
        groupsCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Timber.e(e, "Listen failed.")
                return@addSnapshotListener
            }

            GlobalScope.launch(Dispatchers.IO) {
                for (docChange in snapshots!!.documentChanges) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            Timber.d("Document added: ${docChange.document.data}")
                            val group = docChange.document.toObject(Group::class.java)
                            roomDatasource.insertGroup(group.toGroupTable())
                        }

                        DocumentChange.Type.MODIFIED -> {
                            Timber.d("Document updated: ${docChange.document.data}")
                            val group = docChange.document.toObject(Group::class.java)
                            roomDatasource.updateGroup(group.toGroupTable())
                        }

                        DocumentChange.Type.REMOVED -> {
                            Timber.d("Document deleted: ${docChange.document.id}")
                            val group = docChange.document.toObject(Group::class.java)
                            roomDatasource.deleteGroup(group.toGroupTable())
                        }
                    }
                }
            }
        }
    }

    /**
     * Para añadir un grupo a la base de datos, necesitabamos saber el usuario que lo va a crear.
     * También lo añadimos a la lista de usuarios del grupo.
     */
    override fun createGroup(groupName: String, user: User/*, success: (Group) -> Unit*/) {
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
//                success(group)
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error creating the group")
            }
    }

    companion object {
        const val DB_ROOT_COLLECTION = "FFA"
    }
}

interface FirebaseDatabaseDatasource {
    //    fun createGroup(groupName: String, user: User, success: (Group) -> Unit)
    fun createGroup(groupName: String, user: User)
}
