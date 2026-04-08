package com.apptolast.familyfilmapp.repositories

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMediaStatus
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : Repository {

    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Media>> = flowOf(
        PagingData.from(
            listOf(
                Media().copy(
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

    override suspend fun getPopularMoviesList(page: Int): Result<List<Media>> = Result.success(emptyList())

    override suspend fun searchTmdbMovieByName(string: String): Result<List<Media>> = Result.success(emptyList())

    override suspend fun getMoviesByIds(ids: List<Int>): Result<List<Media>> = Result.success(emptyList())

    override fun getPopularTvShows(pageSize: Int): Flow<PagingData<Media>> = flowOf(
        PagingData.from(
            emptyList(),
            sourceLoadStates = LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
        ),
    )

    override suspend fun getPopularTvShowsList(page: Int): Result<List<Media>> = Result.success(emptyList())

    override suspend fun searchMulti(query: String): Result<List<Media>> = Result.success(emptyList())

    override suspend fun getTvShowsByIds(ids: List<Int>): Result<List<Media>> = Result.success(emptyList())

    override fun getMyGroups(userId: String): Flow<List<Group>> = flowOf(emptyList())

    override suspend fun createGroup(groupName: String, userId: String): Result<Group> = Result.success(Group())

    override suspend fun updateGroup(group: Group): Result<Unit> = Result.success(Unit)

    override suspend fun deleteGroup(groupId: String): Result<Unit> = Result.success(Unit)

    override suspend fun addMember(groupId: String, identifier: String): Result<Unit> = Result.success(Unit)

    override suspend fun removeMember(groupId: String, userId: String): Result<Unit> = Result.success(Unit)

    override suspend fun createUser(user: User): Result<Unit> = Result.success(Unit)

    override fun getUserById(string: String): Flow<User> = flow {
        emit(
            User().copy(
                id = "id1",
                email = "email",
                language = "es_ES",
            ),
        )
    }

    override suspend fun updateUser(user: User): Result<Unit> = Result.success(Unit)

    override suspend fun deleteUser(user: User): Result<Unit> = Result.success(Unit)

    override suspend fun checkIfUserExists(userId: String): Boolean = true

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> = Result.success(emptyList())

    override suspend fun isUsernameAvailable(username: String): Boolean = true

    override suspend fun updateUsername(user: User, newUsername: String): Result<Unit> = Result.success(Unit)

    override suspend fun updateHasRemovedAds(userId: String, hasRemovedAds: Boolean): Result<Unit> =
        Result.success(Unit)

    override suspend fun updateMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        status: MediaStatus,
        mediaType: com.apptolast.familyfilmapp.model.local.types.MediaType,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun removeMovieStatus(
        groupIds: List<String>,
        userId: String,
        movieId: Int,
        mediaType: com.apptolast.familyfilmapp.model.local.types.MediaType,
    ): Result<Unit> = Result.success(Unit)

    override fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMediaStatus>> = flowOf(emptyList())

    override suspend fun getAllMarkedMovieIdsForUser(userId: String): List<Int> = emptyList()

    override fun startSync(userId: String) { /* no-op */ }

    override fun stopSync() { /* no-op */ }

    override fun getSyncState(): Flow<SyncState> = MutableStateFlow(SyncState.Synced)

    override suspend fun clearLocalData() { /* no-op */ }
}
