package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import com.apptolast.familyfilmapp.network.ApiRoutesParams.PAGE_MOVIES
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {

    @GET(MOVIES_POPULAR)
    suspend fun getPopularMovies(
        @Query(PAGE_MOVIES) page: Int,
        @Query(PARAM_ADULT) adult: Boolean = false,
    ): TmdbMovieWrapperRemote

    @GET(SEARCH_MOVIE)
    suspend fun searchMovieByName(
        @Query(PARAM_MOVIE_NAME) movieName: String,
        @Query(PARAM_ADULT) adult: Boolean = false,
    ): TmdbMovieWrapperRemote

    companion object {
        // Params
        const val PARAM_PAGE = "page"
        const val PARAM_MOVIE_NAME = "query"
        const val PARAM_ADULT = "include_adult"

        // Routes
        const val MOVIES_POPULAR = "movie/popular"
        const val SEARCH_MOVIE = "search/movie"
    }
}


