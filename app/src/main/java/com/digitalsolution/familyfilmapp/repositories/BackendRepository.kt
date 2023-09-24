package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.network.BackendApi
import javax.inject.Inject

class BackendRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val localRepository: LocalRepository,
) : BackendRepository {

    override suspend fun login(user: String, fbid: String): Result<Unit> = kotlin.runCatching {
        // Create the body object
        val body = LoginBody(user, fbid)
        // Login the user to our backend
        backendApi.login(body).token.let { token ->
            if (!token.isNullOrBlank()) {
                // Store the user token to authenticate the future requests to our backend
                localRepository.setToken(token)
            } else {
                // Return a throwable just to indicate there was an error that will be handle in the ViewModel
                throw Throwable()
            }
        }
    }

//    override suspend fun getMovies(): Result<List<Movie>> = kotlin.runCatching {
//        backendApi.getMovies().toDomain()
//    }
}

interface BackendRepository {
    suspend fun login(user: String, fbid: String): Result<Unit>
//    suspend fun getMovies(): Result<List<Movie>>
}
