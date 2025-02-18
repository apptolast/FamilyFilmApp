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
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import com.apptolast.familyfilmapp.workers.SyncWorker
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import timber.log.Timber

class RepositoryImpl @Inject constructor(
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
    override fun getMyGroups(userId: String): Flow<List<Group>> =
        roomDatasource.getMyGroups(userId).map { groupTables ->
            groupTables.map { it.toGroup() }
        }

    /**
     * Add new group into the database and store it in room database if successful.
     *
     * @param groupName The name of the group to be created.
     * @param user The user that is creating the group.
     */
    override fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.createGroup(
            groupName = groupName,
            user = user,
            success = success,
            failure = failure,
        )

    override fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.updateGroup(group, success = success, failure = failure)

    override fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.deleteGroup(group, success = success, failure = failure)

    override fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit) =
        firebaseDatabaseDatasource.addMember(group, email, success = success, failure = failure)

    override fun deleteMember(group: Group, user: User) {
        firebaseDatabaseDatasource.deleteMember(
            group,
            user,
            success = { enqueueSyncWork() },
            failure = { Timber.e(it, "Error deleting member from group") },
        )
    }

    private fun enqueueSyncWork() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(syncWorkRequest)
    }

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    override fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit) {
        firebaseDatabaseDatasource.createUser(
            user = user,
            success = success,
            failure = failure,
        )
    }

    override fun getUserById(userId: String): Flow<User> =
        roomDatasource.getUser(userId).filterNotNull().map { it.toUser() }

    override fun updateUser(user: User, success: (Void?) -> Unit) =
        firebaseDatabaseDatasource.updateUser(user,success)
}

interface Repository {

    // Movies
    fun getPopularMovies(pageSize: Int = 1): Flow<PagingData<Movie>>
    suspend fun searchMovieByName(string: String): List<Movie>

    // Groups
    fun getMyGroups(userId: String): Flow<List<Group>>
    fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit)
    fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit)
    fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit)
    fun deleteMember(group: Group, user: User)

    // Users
    fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit)
    fun getUserById(string: String): Flow<User>
    fun updateUser(user: User, success: (Void?) -> Unit)
}
