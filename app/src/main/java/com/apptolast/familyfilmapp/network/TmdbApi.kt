package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import com.apptolast.familyfilmapp.network.ApiRoutesParams.PAGE_MOVIES
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET(MOVIES_POPULAR)
    suspend fun getPopularMovies(
        @Query(PAGE_MOVIES) page: Int,
    ): TmdbMovieWrapperRemote

    companion object {
        // Params
        const val PARAM_PAGE = "page"

        // Routes
        const val MOVIES_POPULAR = "movie/popular"

    }
}


