package com.digitalsolution.familyfilmapp.network

import com.digitalsolution.familyfilmapp.model.remote.request.AddMemberBody
import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.request.RegisterBody
import com.digitalsolution.familyfilmapp.model.remote.response.GenreInfoRemote
import com.digitalsolution.familyfilmapp.model.remote.response.GroupInfoRemote
import com.digitalsolution.familyfilmapp.model.remote.response.MovieRemote
import com.digitalsolution.familyfilmapp.model.remote.response.ResponseWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BackendApi {

    @POST(ApiRoutes.AUTH_REGISTER)
    suspend fun register(
        @Body registerBody: RegisterBody,
    ): ResponseWrapper<Any>

    @POST(ApiRoutes.AUTH_LOGIN)
    suspend fun login(
        @Body loginBody: LoginBody,
    ): ResponseWrapper<Any>

    @GET(ApiRoutes.MOVIES)
    suspend fun getMovies(): ResponseWrapper<List<MovieRemote>>

    @GET(ApiRoutes.GROUPS)
    suspend fun getGroups(): ResponseWrapper<List<GroupInfoRemote>>

    @PATCH(ApiRoutes.GROUP_ADD_MEMBER)
    suspend fun addGroupMember(
        @Path("groupId") groupId: Int,
        @Body memberBody: AddMemberBody,
    ): ResponseWrapper<Any>

    @GET(ApiRoutes.GENRES)
    suspend fun getGenres(): ResponseWrapper<List<GenreInfoRemote>>
}
