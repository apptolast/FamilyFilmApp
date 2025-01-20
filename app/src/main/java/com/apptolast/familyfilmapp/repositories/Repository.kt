package com.apptolast.familyfilmapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.ui.screens.home.MoviePagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
//    private val roomDatasource: RoomDatasource,
//    private val firebaseDatasource: FirebaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
) : Repository {


    override fun getPopularMovies(pageSize: Int): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { MoviePagingSource(tmdbDatasource) },
    ).flow

    override suspend fun searchMovieByName(string: String): List<Movie> =
        tmdbDatasource.searchMovieByName(string).map { it.toDomain() }


//    override suspend fun getPopularMovies(): List<Movie> =
//        tmdbDatasource.getPopularMovies().map { it.toDomain() }

}


interface Repository {

    fun getPopularMovies(pageSize: Int = 1): Flow<PagingData<Movie>>
    suspend fun searchMovieByName(string: String): List<Movie>

}
