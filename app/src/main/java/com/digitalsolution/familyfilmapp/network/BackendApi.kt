package com.digitalsolution.familyfilmapp.network

import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.request.RegisterBody
import com.digitalsolution.familyfilmapp.model.remote.response.LoginResponse
import com.digitalsolution.familyfilmapp.model.remote.response.MovieResponse
import com.digitalsolution.familyfilmapp.model.remote.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendApi {

    @POST(ApiRoutes.AUTH_REGISTER)
    suspend fun register(
        @Body registerBody: RegisterBody
    ): RegisterResponse

    @POST(ApiRoutes.AUTH_LOGIN)
    suspend fun login(
        @Body loginBody: LoginBody
    ): LoginResponse

    @GET(ApiRoutes.MOVIES)
    suspend fun getMovies(): MovieResponse

}
