package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import retrofit2.http.GET

interface TmdbApi {

    @GET(MOVIES_POPULAR)
    suspend fun getPopularMovies(): TmdbMovieWrapperRemote

    companion object {
        const val MOVIES_POPULAR = "movie/popular"
    }
}


