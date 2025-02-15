package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import com.apptolast.familyfilmapp.workers.SyncWorker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
    private val workManager: WorkManager,
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
    override fun getMyGroups(userId: String): Flow<List<Group>> {
//        return roomDatasource.getGroups().map { groupTables ->
//            groupTables.map { it.toGroup() }
//        }
        return roomDatasource.getMyGroups(userId).map { groupTables ->
            groupTables.map { it.toGroup() }
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
            val currentUser = getCurrentUser()
            firebaseDatabaseDatasource.createGroup(groupName, currentUser) { groupAdded ->
                enqueueSyncWork()
            }
        }
    }

    override fun updateGroup(viewModelScope: CoroutineScope, group: Group) {
        viewModelScope.launch {
            firebaseDatabaseDatasource.updateGroup(
                group,
                success = { enqueueSyncWork() },
                failure = { Timber.e(it, "Error updating group") },
            )
        }
    }

    override fun deleteGroup(viewModelScope: CoroutineScope, group: Group, onFinally: () -> Unit) {
        viewModelScope.launch {
            firebaseDatabaseDatasource.deleteGroup(
                group,
                success = { enqueueSyncWork() },
                failure = { Timber.e(it, "Error deleting group") },
            )
        }.invokeOnCompletion { onFinally() }
    }

    override fun addMember(viewModelScope: CoroutineScope, group: Group, email: String) {
        viewModelScope.launch {
            firebaseDatabaseDatasource.addMember(
                group, email,
                success = { enqueueSyncWork() },
                failure = { Timber.e(it, "Error adding member to group") },
            )
        }
    }

    override fun deleteMember(viewModelScope: CoroutineScope, group: Group, user: User) {
        viewModelScope.launch {
            firebaseDatabaseDatasource.deleteMember(
                group, user,
                success = { enqueueSyncWork() },
                failure = { Timber.e(it, "Error deleting member from group") },
            )
        }
    }

    private fun enqueueSyncWork() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(syncWorkRequest)
    }

    private suspend fun getCurrentUser(): User =
        roomDatasource.getUser(firebaseAuth.currentUser?.uid!!).first()!!.toUser()


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
    fun createGroup(viewModelScope: CoroutineScope, groupName: String)
    fun updateGroup(viewModelScope: CoroutineScope, group: Group)
    fun deleteGroup(viewModelScope: CoroutineScope, group: Group, onFinally: () -> Unit)
    fun addMember(viewModelScope: CoroutineScope, group: Group, email: String)
    fun deleteMember(viewModelScope: CoroutineScope, group: Group, user: User)

    // Users
    suspend fun createUser(viewModelScope: CoroutineScope, user: User)
    fun getUserById(string: String): Flow<User>
    fun updateUser(viewModelScope: CoroutineScope, user: User)
}
