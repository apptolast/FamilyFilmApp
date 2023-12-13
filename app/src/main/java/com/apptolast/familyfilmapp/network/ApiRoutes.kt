package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM

object ApiRoutes {
    const val AUTH_REGISTER = "auth/register"
    const val AUTH_LOGIN = "auth/login"
    const val MOVIES = "movies"
    const val GROUPS = "groups"
    const val GROUP = "$GROUPS/{$GROUP_ID_PARAM}"
    const val ADD_MEMBER = "$GROUPS/{$GROUP_ID_PARAM}/addMember"
    const val REMOVE_MEMBER_FROM_GROUP = "$GROUPS/{$GROUP_ID_PARAM}/removeMember"
    const val GENRES = "genres"
}

object ApiRoutesParams {
    const val GROUP_ID_PARAM = "group_id"
}
