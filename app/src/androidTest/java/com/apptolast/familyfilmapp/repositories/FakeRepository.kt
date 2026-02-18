package com.apptolast.familyfilmapp.repositories

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository(
    private val roomDatasource: RoomDatasource,
    private val firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
) : Repository {

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

//    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = Pager(
//        config = PagingConfig(pageSize),
//        pagingSourceFactory = { MoviePagingSource(tmdbDatasource) },
//    ).flow

    override suspend fun searchTmdbMovieByName(string: String): List<Movie> {
        TODO("Not yet implemented")
    }

    override suspend fun getMoviesByIds(ids: List<Int>): Result<List<Movie>> {
        TODO("Not yet implemented")
    }

    override fun getMyGroups(userId: String): Flow<List<Group>> {
        TODO("Not yet implemented")
    }

    override fun createGroup(groupName: String, user: User, success: (Group) -> Unit, failure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteGroup(group: Group, success: () -> Unit, failure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun addMember(group: Group, email: String, success: () -> Unit, failure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteMember(group: Group, user: User) {
        TODO("Not yet implemented")
    }

    override fun createUser(user: User, success: (Void?) -> Unit, failure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getUserById(string: String): Flow<User> = flow {
        User().copy(
            id = "id1",
            email = "email",
            language = "es_ES",
            statusMovies = mapOf(),
        )
    }

    override fun updateUser(user: User, success: (Void?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(user: User, success: () -> Unit, failure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun clearLocalData() {
        // No-op in tests
    }
}
