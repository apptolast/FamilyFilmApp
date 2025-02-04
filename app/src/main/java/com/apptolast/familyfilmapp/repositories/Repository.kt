package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.model.room.toGroup
import com.apptolast.familyfilmapp.model.room.toUser
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
        return roomDatasource.getGroups()
            .map { groupTableList ->
                // Obtener la lista de users para cada grupo ya que groupTable.users esta Ignore

                val groupUsers = roomDatasource.getus

                groupTableList.filter { groupTable ->
                    groupTable.users.any { user -> user.userId == userId }
                }.map { it.toGroup() }

            // Getting the groups where the user is part of the list of users
            // But we need to populate de userList not retrieved from room,

//            myGroupTableList.map { myGroupTable ->
//                val users = mutableListOf<User>()
//                myGroupTable.users.map {
//                    roomDatasource.getUser(it.userId).first()?.toUser().let { user ->
//                        if (user != null) {
//                            users.add(user)
//                        } else {
//                            Timber.w("User not found in Room. Look for it in Firebase")
//                        }
//                    }
//                }
//            }
        }
    }


//        roomDatasource.getGroupsById(getCurrentUser().id).flatMap {
//            it.toG() }


    /**
     * Add new group into the database and store it in room database if successful.
     *
     * @param viewModelScope The scope where the operation will be executed.
     * @param groupName The name of the group to be created.
     */
    override fun createGroup(viewModelScope: CoroutineScope, groupName: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            firebaseDatabaseDatasource.createGroup(groupName, currentUser)

//            { newGroup ->
//                viewModelScope.launch {
//                    roomDatasource.insertGroup(newGroup.toGroupTable())
//                }
//            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
// Users
///////////////////////////////////////////////////////////////////////////
    override suspend fun getCurrentUser(): User =
        roomDatasource.getUser(firebaseAuth.currentUser?.uid!!).first()!!.toUser()

}


interface Repository {

    // Movies
    fun getPopularMovies(pageSize: Int = 1): Flow<PagingData<Movie>>
    suspend fun searchMovieByName(string: String): List<Movie>

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    fun createGroup(viewModelScope: CoroutineScope, groupName: String)

    // Users
    suspend fun getCurrentUser(): User

}
