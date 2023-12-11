package com.digitalsolution.familyfilmapp.network

import com.digitalsolution.familyfilmapp.model.remote.request.AddGroupBody
import com.digitalsolution.familyfilmapp.model.remote.request.AddMemberBody
import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.request.RegisterBody
import com.digitalsolution.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.digitalsolution.familyfilmapp.model.remote.response.AddGroupRemote
import com.digitalsolution.familyfilmapp.model.remote.response.AddMemberRemote
import com.digitalsolution.familyfilmapp.model.remote.response.GenreInfoRemote
import com.digitalsolution.familyfilmapp.model.remote.response.GroupInfoRemote
import com.digitalsolution.familyfilmapp.model.remote.response.MovieRemote
import com.digitalsolution.familyfilmapp.model.remote.response.ResponseWrapper
import com.digitalsolution.familyfilmapp.model.remote.response.UpdateGroupRemote
import com.digitalsolution.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendApi {

    @POST(ApiRoutes.AUTH_REGISTER)
    suspend fun register(@Body registerBody: RegisterBody): ResponseWrapper<Any>

    @POST(ApiRoutes.AUTH_LOGIN)
    suspend fun login(@Body loginBody: LoginBody): ResponseWrapper<Any>

    @GET(ApiRoutes.MOVIES)
    suspend fun getMovies(): ResponseWrapper<List<MovieRemote>>

    @GET(ApiRoutes.GROUPS)
    suspend fun getGroups(): ResponseWrapper<List<GroupInfoRemote>>

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
    suspend fun removeMemberFromGroup(
        @Path(GROUP_ID_PARAM)  groupId: Int,
        @Body userId: Int
    ): ResponseWrapper<Unit>

    @GET(ApiRoutes.GENRES)
    suspend fun getGenres(): ResponseWrapper<List<GenreInfoRemote>>
}
