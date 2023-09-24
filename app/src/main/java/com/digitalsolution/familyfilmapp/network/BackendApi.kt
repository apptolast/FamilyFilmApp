package com.digitalsolution.familyfilmapp.network

import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendApi {

    @POST(ApiRoutes.USER_LOGIN)
    suspend fun login(
        @Body loginBody: LoginBody
    ): LoginResponse

//    @GET(ApiRoutes.MOVIES)
//    suspend fun getMovies(): List<MovieWrapper>

}
