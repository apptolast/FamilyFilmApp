package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM
import com.apptolast.familyfilmapp.network.ApiRoutesParams.LANGUAGE

object ApiRoutes {
    const val AUTH_LOGIN = "user/login"
    const val MOVIES = "movie"
    const val GROUPS = "group/all/{$LANGUAGE}"
    const val GROUP = "$GROUPS/{$GROUP_ID_PARAM}"
    const val ADD_MEMBER = "$GROUPS/{$GROUP_ID_PARAM}/addMember"
    const val REMOVE_MEMBER_FROM_GROUP = "$GROUPS/{$GROUP_ID_PARAM}/removeMember"
    const val ADD_MOVIE_TO_WATCHLIST = "$GROUPS/{$GROUP_ID_PARAM}/addWatch"
    const val ADD_MOVIE_TO_SEEN = "$GROUPS/{$GROUP_ID_PARAM}/addView"
    const val GENRES = "genres"
}

object ApiRoutesParams {
    const val GROUP_ID_PARAM = "group_id"
    const val LANGUAGE = "idiom"
}
