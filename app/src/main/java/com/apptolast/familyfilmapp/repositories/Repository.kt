package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
) : Repository {

    ///////////////////////////////////////////////////////////////////////////
    // Movies
    ///////////////////////////////////////////////////////////////////////////
    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { MoviePagingSource(tmdbDatasource) },
    ).flow

    override suspend fun searchMovieByName(string: String): List<Movie> =
        tmdbDatasource.searchMovieByName(string).map { it.toDomain() }


    ///////////////////////////////////////////////////////////////////////////
    // Groups
    ///////////////////////////////////////////////////////////////////////////
    override fun getMyGroups(userId: String): Flow<List<Group>> {
        return roomDatasource.getGroups().map { groupsTable ->
            groupsTable.map { it.toGroup() }
        }
    }

    /**
     * Add new group into the database and store it in room database if successful.
     *
     * @param viewModelScope The scope where the operation will be executed.
     * @param groupName The name of the group to be created.
     */
    override fun createGroup(viewModelScope: CoroutineScope, groupName: String) {
        viewModelScope.launch {
            val currentUser = getUserById(firebaseAuth.currentUser!!.uid).first()
            firebaseDatabaseDatasource.createGroup(groupName, currentUser) { groupAdded ->
                // Update room with the new group after adding it to the database successfully
                viewModelScope.launch {
                    roomDatasource.insertGroup(groupAdded.toGroupTable())
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

    override fun deleteGroup(viewModelScope: CoroutineScope, group: Group) {
        firebaseDatabaseDatasource.deleteGroup(group) {
            viewModelScope.launch {
                roomDatasource.deleteGroup(group.toGroupTable())
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

                if(user == null) {
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
                // TODO: Notify that the user is already in the group
                val a = 1
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Users
    ///////////////////////////////////////////////////////////////////////////
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

//    override suspend fun getCurrentUser(): User =
//        roomDatasource.getUser(firebaseAuth.currentUser?.uid!!).first()!!.toUser()

    override suspend fun getUserById(userId: String): Flow<User> {
        val user = roomDatasource.getUser(userId).first()?.toUser()

        // If user is null, look for the user in firestore database.
        return if (user != null) {
            flowOf(user)
        } else {
            callbackFlow<User> {
                val callback: (User?) -> Unit = { user ->
                    trySend(user!!)
                }
                firebaseDatabaseDatasource.getUserById(userId, callback)
                awaitClose { }
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
    fun createGroup(viewModelScope: CoroutineScope, groupName: String)
    fun updateGroup(viewModelScope: CoroutineScope, group: Group)
    fun deleteGroup(viewModelScope: CoroutineScope, group: Group)
    fun addMember(viewModelScope: CoroutineScope, group: Group, email: String)

    // Users
    suspend fun createUser(viewModelScope: CoroutineScope, user: User)

    //    suspend fun getCurrentUser(): User
    suspend fun getUserById(string: String): Flow<User>

}
