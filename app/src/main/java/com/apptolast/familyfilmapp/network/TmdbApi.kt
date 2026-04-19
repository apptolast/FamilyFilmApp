package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchWrapperRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowWrapperRemote
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Locale

interface TmdbApi {

    @GET(MOVIES_POPULAR)
    suspend fun getPopularMovies(
        @Query(PARAM_PAGE) page: Int,
        @Query(PARAM_LANGUAGE) language: String = Locale.getDefault().toLanguageTag(),
        @Query(PARAM_PROVIDERS) providers: String = PARAM_PROVIDERS_VALUE,
    ): TmdbMovieWrapperRemote

    @GET(SEARCH_MOVIE)
    suspend fun searchMovieByName(
        @Query(PARAM_MOVIE_NAME) movieName: String,
        @Query(PARAM_ADULT) adult: Boolean,
        @Query(PARAM_LANGUAGE) language: String = Locale.getDefault().toLanguageTag(),
    ): TmdbMovieWrapperRemote

    @GET("$MOVIE/{$PARAM_MOVIE_ID}")
    suspend fun searchMovieById(
        @Path(PARAM_MOVIE_ID) movieId: Int,
        @Query(PARAM_LANGUAGE) language: String = Locale.getDefault().toLanguageTag(),
        @Query(PARAM_PROVIDERS) providers: String = PARAM_PROVIDERS_VALUE,
    ): TmdbMovieRemote

    @GET(TV_POPULAR)
    suspend fun getPopularTvShows(
        @Query(PARAM_PAGE) page: Int,
        @Query(PARAM_LANGUAGE) language: String = Locale.getDefault().toLanguageTag(),
        @Query(PARAM_PROVIDERS) providers: String = PARAM_PROVIDERS_VALUE,
    ): TmdbTvShowWrapperRemote

    @GET("$TV/{$PARAM_TV_ID}")
    suspend fun getTvShowById(
        @Path(PARAM_TV_ID) tvId: Int,
        @Query(PARAM_LANGUAGE) language: String = Locale.getDefault().toLanguageTag(),
        @Query(PARAM_PROVIDERS) providers: String = PARAM_PROVIDERS_VALUE,
    ): TmdbTvShowRemote

    @GET(SEARCH_MULTI)
    suspend fun searchMulti(
        @Query(PARAM_MOVIE_NAME) query: String,
        @Query(PARAM_ADULT) adult: Boolean,
        @Query(PARAM_LANGUAGE) language: String = Locale.getDefault().toLanguageTag(),
    ): TmdbMultiSearchWrapperRemote

    companion object {
        // Params
        const val PARAM_PAGE = "page"
        const val PARAM_MOVIE_NAME = "query"
        const val PARAM_MOVIE_ID = "movie_id"
        const val PARAM_ADULT = "include_adult"
        const val PARAM_LANGUAGE = "language"
        const val PARAM_PROVIDERS = "append_to_response"
        const val PARAM_PROVIDERS_VALUE = "watch/providers"

        // Routes
        const val MOVIES_POPULAR = "movie/popular"
        const val SEARCH_MOVIE = "search/movie"
        const val MOVIE = "movie"
        const val TV_POPULAR = "tv/popular"
        const val TV = "tv"
        const val PARAM_TV_ID = "tv_id"
        const val SEARCH_MULTI = "search/multi"
    }
}
