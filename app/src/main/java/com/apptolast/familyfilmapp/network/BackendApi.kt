package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.request.AddGroupBody
import com.apptolast.familyfilmapp.model.remote.request.AddMemberBody
import com.apptolast.familyfilmapp.model.remote.request.AddMovieWatchListBody
import com.apptolast.familyfilmapp.model.remote.request.LoginBody
import com.apptolast.familyfilmapp.model.remote.request.RemoveMemberBody
import com.apptolast.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.apptolast.familyfilmapp.model.remote.response.AddGroupRemote
import com.apptolast.familyfilmapp.model.remote.response.AddMemberRemote
import com.apptolast.familyfilmapp.model.remote.response.GenreInfoRemote
import com.apptolast.familyfilmapp.model.remote.response.GroupInfoRemote
import com.apptolast.familyfilmapp.model.remote.response.MovieRemote
import com.apptolast.familyfilmapp.model.remote.response.ResponseWrapper
import com.apptolast.familyfilmapp.model.remote.response.UpdateGroupRemote
import com.apptolast.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM
import com.apptolast.familyfilmapp.network.ApiRoutesParams.LANGUAGE
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendApi {

    @POST(ApiRoutes.AUTH_LOGIN)
    suspend fun login(@Body loginBody: LoginBody): ResponseWrapper<Unit>

    @GET(ApiRoutes.MOVIES)
    suspend fun getMovies(): ResponseWrapper<List<MovieRemote>>

    @GET(ApiRoutes.GROUPS)
    suspend fun getGroups(@Path(LANGUAGE) idiom: String): ResponseWrapper<List<GroupInfoRemote>>

    @POST(ApiRoutes.GROUPS)
    suspend fun addGroups(@Body addGroupBody: AddGroupBody): ResponseWrapper<AddGroupRemote>

    @DELETE(ApiRoutes.GROUP)
    suspend fun deleteGroup(@Path(GROUP_ID_PARAM) groupId: Int)

    @PUT(ApiRoutes.GROUP)
    suspend fun updateGroupName(
        @Path(GROUP_ID_PARAM) groupId: Int,
        @Body updateGroupNameBody: UpdateGroupNameBody,
    ): ResponseWrapper<UpdateGroupRemote>

    @PATCH(ApiRoutes.ADD_MEMBER)
    suspend fun addMemberGroup(
        @Path(GROUP_ID_PARAM) groupId: Int,
        @Body addMemberBody: AddMemberBody,
    ): ResponseWrapper<AddMemberRemote>

    @PATCH(ApiRoutes.REMOVE_MEMBER_FROM_GROUP)
    suspend fun removeMemberFromGroup(@Path(GROUP_ID_PARAM) groupId: Int, @Body removeMemberBody: RemoveMemberBody)

    @PATCH(ApiRoutes.ADD_MOVIE_TO_WATCHLIST)
    suspend fun addMovieToWatchList(
        @Path(GROUP_ID_PARAM) groupId: Int,
        @Body addMovieWatchListBody: AddMovieWatchListBody,
    )

    @PATCH(ApiRoutes.ADD_MOVIE_TO_SEEN)
    suspend fun addMovieToSeenList(
        @Path(GROUP_ID_PARAM) groupId: Int,
        @Body addMovieSeenListBody: AddMovieWatchListBody,
    )

    @GET(ApiRoutes.GENRES)
    suspend fun getGenres(): ResponseWrapper<List<GenreInfoRemote>>
}
