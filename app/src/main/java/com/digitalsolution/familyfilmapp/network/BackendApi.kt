package com.digitalsolution.familyfilmapp.network

import com.digitalsolution.familyfilmapp.model.remote.request.AddGroupBody
import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.request.RegisterBody
import com.digitalsolution.familyfilmapp.model.remote.response.AddGroupRemote
import com.digitalsolution.familyfilmapp.model.remote.response.GenreInfoRemote
import com.digitalsolution.familyfilmapp.model.remote.response.GroupInfoRemote
import com.digitalsolution.familyfilmapp.model.remote.response.MovieRemote
import com.digitalsolution.familyfilmapp.model.remote.response.ResponseWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    @POST(ApiRoutes.GROUPS)
    suspend fun addGroups(
        @Body addGroupBody: AddGroupBody,
    ): ResponseWrapper<AddGroupRemote>

    @GET(ApiRoutes.GENRES)
    suspend fun getGenres(): ResponseWrapper<List<GenreInfoRemote>>
}
