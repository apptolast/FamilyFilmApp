package com.apptolast.familyfilmapp.repositories

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMovieStatus
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : Repository {

    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = flowOf(
        PagingData.from(
            listOf(
                Movie().copy(
                    title = "Matrix",
                    overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                    """.trimIndent(),
                    posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                ),
            ),
            sourceLoadStates = LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
        ),
    )

    override suspend fun getPopularMoviesList(page: Int): Result<List<Movie>> =
        Result.success(emptyList())

    override suspend fun searchTmdbMovieByName(string: String): Result<List<Movie>> =
        Result.success(emptyList())

    override suspend fun getMoviesByIds(ids: List<Int>): Result<List<Movie>> =
        Result.success(emptyList())

    override fun getMyGroups(userId: String): Flow<List<Group>> = flowOf(emptyList())

    override suspend fun createGroup(groupName: String, userId: String): Result<Group> =
        Result.success(Group())

    override suspend fun updateGroup(group: Group): Result<Unit> = Result.success(Unit)

    override suspend fun deleteGroup(groupId: String): Result<Unit> = Result.success(Unit)

    override suspend fun addMember(groupId: String, email: String): Result<Unit> = Result.success(Unit)

    override suspend fun removeMember(groupId: String, userId: String): Result<Unit> = Result.success(Unit)

    override suspend fun createUser(user: User): Result<Unit> = Result.success(Unit)

    override fun getUserById(string: String): Flow<User> = flow {
        emit(
            User().copy(
                id = "id1",
                email = "email",
                language = "es_ES",
                statusMovies = mapOf(),
            ),
        )
    }

    override suspend fun updateUser(user: User): Result<Unit> = Result.success(Unit)

    override suspend fun deleteUser(user: User): Result<Unit> = Result.success(Unit)

    override suspend fun checkIfUserExists(userId: String): Boolean = true

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> =
        Result.success(emptyList())

    override suspend fun updateMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        status: MovieStatus,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun removeMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
    ): Result<Unit> = Result.success(Unit)

    override fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMovieStatus>> =
        flowOf(emptyList())

    override suspend fun getAllMarkedMovieIdsForUser(userId: String): List<Int> = emptyList()

    override fun startSync(userId: String) { /* no-op */ }

    override fun stopSync() { /* no-op */ }

    override fun getSyncState(): Flow<SyncState> = MutableStateFlow(SyncState.Synced)

    override suspend fun clearLocalData() { /* no-op */ }
}