package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM
import com.apptolast.familyfilmapp.network.ApiRoutesParams.MOVIE_ID_PARAM
import com.apptolast.familyfilmapp.network.ApiRoutesParams.MOVIE_FILTER
import com.apptolast.familyfilmapp.network.ApiRoutesParams.PAGE_MOVIES
import com.apptolast.familyfilmapp.network.ApiRoutesParams.USER_ID_PARAM

object ApiRoutes {

    // /////////////////////////////////////////////////////////////////////////
    // NEW
    // /////////////////////////////////////////////////////////////////////////
    const val REGISTER = "auth/register"

    const val MOVIES = "moviesandseries/movies/popular"
    const val MOVIES_BY_ID = "movie/getAllMoviesFromIDS"
    const val MOVIES_SEARCH_NAME = "moviesandseries/search/{$MOVIE_FILTER}/{$PAGE_MOVIES}/"
    const val SERIES = "moviesandseries/series/popular"

    const val GROUPS_DETAILS_MOVIE_DIALOG = "groups/movie/{$MOVIE_ID_PARAM}/status"
    const val GROUPS_ADD_MOVIE = "groups/addMovie"

    // /////////////////////////////////////////////////////////////////////////
    // OLD
    // /////////////////////////////////////////////////////////////////////////
    const val ME = "users/me"

    //    const val USER_CREATE = "users"
//    const val MOVIES = "movie"
    const val GROUPS = "groups"
    const val CREATE_GROUP = "groups"
    const val REMOVE_GROUP = "groups/{$GROUP_ID_PARAM}"
    const val EDIT_GROUP_NAME = "groups/{$GROUP_ID_PARAM}"
    const val ADD_MEMBER = "$GROUPS/addUser/{$GROUP_ID_PARAM}"
    const val REMOVE_MEMBER = "$GROUPS/{$GROUP_ID_PARAM}/user/{$USER_ID_PARAM}"
    const val REMOVE_MEMBER_FROM_GROUP = "$GROUPS/{$GROUP_ID_PARAM}/removeMember"
    const val ADD_MOVIE_TO_WATCHLIST = "$GROUPS/{$GROUP_ID_PARAM}/ToWatch/{$MOVIE_ID_PARAM}"
    const val ADD_MOVIE_TO_SEEN = "$GROUPS/{$GROUP_ID_PARAM}/ToWatched/{$MOVIE_ID_PARAM}"

    const val MOVIES_CATALOGUE = "movies/catalogue/{$PAGE_MOVIES}"
    const val MOVIES_RECOMMENDED = "movies/{$GROUP_ID_PARAM}/recommended"
//    const val MOVIES_SEARCH_NAME = "movies/{$PAGE_MOVIES}/{$MOVIE_NAME}"
    const val GENRES = "genres"
}

object ApiRoutesParams {
    const val GROUP_ID_PARAM = "id"
    const val USER_ID_PARAM = "userId"
    const val MOVIE_ID_PARAM = "movieId"
    const val PAGE_MOVIES = "page"
    const val MOVIE_FILTER = "query"
}
