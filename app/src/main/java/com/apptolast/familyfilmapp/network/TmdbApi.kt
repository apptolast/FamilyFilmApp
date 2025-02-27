package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET(MOVIES_POPULAR)
    suspend fun getPopularMovies(
        @Query(PARAM_PAGE) page: Int,
        @Query(PARAM_ADULT) adult: Boolean = false,
    ): TmdbMovieWrapperRemote

    @GET(SEARCH_MOVIE)
    suspend fun searchMovieByName(
        @Query(PARAM_MOVIE_NAME) movieName: String,
        @Query(PARAM_ADULT) adult: Boolean = false,
    ): TmdbMovieWrapperRemote

    @GET("$MOVIE/{$PARAM_MOVIE_ID}")
    suspend fun searchMovieById(
        @Path(PARAM_MOVIE_ID) movieId: Int,
        @Query(PARAM_ADULT) adult: Boolean = false,
    ): TmdbMovieRemote

    companion object {
        // Params
        const val PARAM_PAGE = "page"
        const val PARAM_MOVIE_NAME = "query"
        const val PARAM_MOVIE_ID = "movie_id"
        const val PARAM_ADULT = "include_adult"

        // Routes
        const val MOVIES_POPULAR = "movie/popular"
        const val SEARCH_MOVIE = "search/movie"
        const val MOVIE = "movie"
    }
}
