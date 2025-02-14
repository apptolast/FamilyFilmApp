package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.extensions.updateModificationDate
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.remote.firebase.toGroup
import com.apptolast.familyfilmapp.model.remote.firebase.toGroupTable
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasourceImpl.Companion.MINIMUM_UPDATE_TIME
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.mutableListOf

class RepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
) : Repository {

    // /////////////////////////////////////////////////////////////////////////
    // Movies
    // /////////////////////////////////////////////////////////////////////////
    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { MoviePagingSource(tmdbDatasource) },
    ).flow

    override suspend fun searchMovieByName(string: String): List<Movie> =
        tmdbDatasource.searchMovieByName(string).map { it.toDomain() }

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    override fun getMyGroups(userId: String): Flow<List<Group>> = channelFlow {
        roomDatasource.getGroups().collectLatest { groups ->
            val needsUpdate = groups.any {
                val timeDiff = Calendar.getInstance().time.time - (it.lastUpdated?.time ?: 0)
                timeDiff > MINIMUM_UPDATE_TIME
            }

            if (needsUpdate) {
                launch {
                    // Update room's groups from firebase
                    val firebaseGroups = firebaseDatabaseDatasource.getMyGroups(userId).first()
                    val groups = mutableListOf<Group>()

                    firebaseGroups.filterNotNull().forEach { groupFirebase ->

                        val users = groupFirebase.users.map { userId ->
                            getUserById(userId).first()
                        }
                        val group = groupFirebase.toGroup().copy(users = users)

                        roomDatasource.updateGroup(
                            group.updateModificationDate().toGroupTable(),
                        )
                        groups.add(group)
                    }
                    send(groups)
                }
            } else {
                launch {
                    send(groups.map { it.toGroup() })
                }
            }
        }
    }
//    override fun getMyGroups(userId: String): Flow<List<Group>> = channelFlow {
//        roomDatasource.getGroups().collectLatest { groups ->
//            val needsUpdate = groups.any {
//                val timeDiff = Calendar.getInstance().time.time - (it.lastUpdated?.time ?: 0)
//                timeDiff > MINIMUM_UPDATE_TIME
//            }
//
//            if (needsUpdate) {
//                launch {
//                    // Update room's groups from firebase
//                    val firebaseGroups = firebaseDatabaseDatasource.getMyGroups(userId).first()
//                    val groups = mutableListOf<Group>()
//
//                    firebaseGroups.filterNotNull().forEach { groupFirebase ->
//
//                        val users = groupFirebase.users.map { userId ->
//                            getUserById(userId).first()
//                        }
//                        val group = groupFirebase.toGroup().copy(users = users)
//
//                        roomDatasource.updateGroup(
//                            group.updateModificationDate().toGroupTable(),
//                        )
//                        groups.add(group)
//                    }
//                    send(groups)
//                }
//            } else {
//                launch {
//                    send(groups.map { it.toGroup() })
//                }
//            }
//        }
//    }

    /**
     * Add new group into the database and store it in room database if successful.
     *
     * @param viewModelScope The scope where the operation will be executed.
     * @param groupName The name of the group to be created.
     */
    override fun createGroup(viewModelScope: CoroutineScope, groupName: String, success: () -> Unit) {
        viewModelScope.launch {
            val currentUser = getUserById(firebaseAuth.currentUser!!.uid).first()
            firebaseDatabaseDatasource.createGroup(groupName, currentUser) { groupAdded ->
                // Update room with the new group after adding it to the database successfully
                viewModelScope.launch {
                    roomDatasource.insertGroup(groupAdded.toGroupTable(this@RepositoryImpl))
                    success.invoke()
                }
            }
        }
    }

    override fun updateGroup(viewModelScope: CoroutineScope, group: Group) {
        firebaseDatabaseDatasource.updateGroup(group) {
            viewModelScope.launch {
                roomDatasource.updateGroup(group.toGroupTable())
            }
        }
    }

    override fun deleteGroup(viewModelScope: CoroutineScope, group: Group, success: () -> Unit  ) {
        firebaseDatabaseDatasource.deleteGroup(group) {
            viewModelScope.launch {
                roomDatasource.deleteGroup(group.toGroupTable())
                success.invoke()
            }
        }
    }

    override fun addMember(viewModelScope: CoroutineScope, group: Group, email: String) {
        // check if it the user is already added in group's users list
        val userAlreadyAdded = email in group.users.map { it.email }

        viewModelScope.launch {
            // If not, include the user in the group and update the group in both databases
            if (!userAlreadyAdded) {
                var user: User? = null

                // First, try to get the cache user from room
                roomDatasource.getUserByEmail(email).first()?.toUser().let { retrieveUser ->
                    Timber.d("User from room: $this")
                    user = retrieveUser

                    if (retrieveUser == null) {
                        // If user is null, retrieve the user from firestore and cache it in room for future use
                        firebaseDatabaseDatasource.getUserByEmail(email) { retrieveUser ->
                            Timber.d("User from firestore: $retrieveUser")
                            user = retrieveUser

                            viewModelScope.launch {
                                retrieveUser?.toUserTable()?.let {
                                    roomDatasource.insertUser(it)
                                    Timber.d("User inserted in room")
                                }
                            }
                        }
                    }
                }

                if (user == null) {
                    // TODO: Notify that the user do not exist in the app and cannot be added
                    return@launch
                }

                // Update group with the retrieved user
                val updateGroup = group.copy(
                    users = group.users.toMutableList().apply {
                        add(user!!)
                    },
                )

                firebaseDatabaseDatasource.updateGroup(updateGroup) {
                    Timber.d("Group updated in firestore")
                    viewModelScope.launch {
                        roomDatasource.updateGroup(updateGroup.toGroupTable())
                        Timber.d("Group updated in room")
                    }
                }
            } else {
                Timber.w("TODO: Notify that the user is already in the group")
            }
        }
    }

    override fun deleteMember(viewModelScope: CoroutineScope, group: Group, user: User) {
        // Delete user from group
        val updateGroup = group.copy(
            users = group.users.toMutableList().apply {
                remove(user)
            },
        )

        // Update group in firebase
        firebaseDatabaseDatasource.updateGroup(updateGroup) {
            // if success delete user from room
            viewModelScope.launch {
                roomDatasource.updateGroup(updateGroup.toGroupTable())
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
// Users
// /////////////////////////////////////////////////////////////////////////
    override suspend fun createUser(viewModelScope: CoroutineScope, user: User) {
        firebaseDatabaseDatasource.createUser(
            user = user,
            success = {
                Timber.d("User created in firestore database")
                viewModelScope.launch {
                    roomDatasource.insertUser(user.toUserTable())
                    Timber.d("User created in room database")
                }
            },
            failure = { ex -> Timber.e(ex) },
        )
    }

    override fun getUserById(userId: String): Flow<User> =
        roomDatasource.getUser(userId).map { userTable ->

            val user = userTable?.toUser()

            // If user is null, look for the user in firestore database.
            (if (user != null) {
                user
            } else {
                callbackFlow<User> {
                    val callback: (User?) -> Unit = { user ->
                        trySend(user!!)
                    }
                    firebaseDatabaseDatasource.getUserById(userId, callback)
                    awaitClose { }
                }
            }) as User
        }


    override fun updateUser(viewModelScope: CoroutineScope, user: User) {
        firebaseDatabaseDatasource.updateUser(user) {
            viewModelScope.launch {
                roomDatasource.updateUser(user.toUserTable())
            }
        }
    }
}

interface Repository {

    // Movies
    fun getPopularMovies(pageSize: Int = 1): Flow<PagingData<Movie>>
    suspend fun searchMovieByName(string: String): List<Movie>

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    fun createGroup(viewModelScope: CoroutineScope, groupName: String, success: () -> Unit)
    fun updateGroup(viewModelScope: CoroutineScope, group: Group)
    fun deleteGroup(viewModelScope: CoroutineScope, group: Group, success: () -> Unit)
    fun addMember(viewModelScope: CoroutineScope, group: Group, email: String)
    fun deleteMember(viewModelScope: CoroutineScope, group: Group, user: User)

    // Users
    suspend fun createUser(viewModelScope: CoroutineScope, user: User)
    fun getUserById(string: String): Flow<User>
    fun updateUser(viewModelScope: CoroutineScope, user: User)
}
