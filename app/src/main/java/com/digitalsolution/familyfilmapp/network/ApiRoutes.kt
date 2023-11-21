package com.digitalsolution.familyfilmapp.network

import com.digitalsolution.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM

object ApiRoutes {
    const val AUTH_REGISTER = "auth/register"
    const val AUTH_LOGIN = "auth/login"
    const val MOVIES = "movies"
    const val GROUPS = "groups"
    const val GROUP = "$GROUPS/{$GROUP_ID_PARAM}"
    const val GENRES = "genres"
}


object ApiRoutesParams{
    const val GROUP_ID_PARAM = "group_id"
}
