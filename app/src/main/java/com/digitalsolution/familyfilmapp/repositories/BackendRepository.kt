package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.local.sealed.StatusResponse
import com.digitalsolution.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.request.RegisterBody
import com.digitalsolution.familyfilmapp.network.BackendApi
import javax.inject.Inject

class BackendRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val localRepository: LocalRepository,
) : BackendRepository {

    override suspend fun register(user: String, firbaseId: String): Result<Unit> = kotlin.runCatching {
        // Create the body object
        val body = RegisterBody(user, firbaseId)
        // Login the user to our backend
        backendApi.register(body).let { response ->
            if (response.status == StatusResponse.SUCCESS.value) {
                // Store the user token to authenticate the future requests to our backend
                localRepository.setToken(response.token)
            } else {
                // Return a throwable just to indicate there was an error that will be handle in the ViewModel
                throw Throwable()
            }
        }
    }

    override suspend fun login(user: String, firebaseId: String): Result<Unit> = kotlin.runCatching {
        // Create the body object
        val body = LoginBody(user, firebaseId)
        // Login the user to our backend
        backendApi.login(body).let { response ->
            if (response.status == StatusResponse.SUCCESS.value) {
                // Store the user token to authenticate the future requests to our backend
                localRepository.setToken(response.token)
            } else {
                // Return a throwable just to indicate there was an error that will be handle in the ViewModel
                throw Throwable()
            }
        }
    }

    override suspend fun getMovies(): Result<List<Movie>> = kotlin.runCatching {
        backendApi.getMovies().let { movieResponse ->
            movieResponse.data?.map { movieItem ->
                movieItem.toDomain()
            } ?: emptyList()
        }
    }

    override suspend fun getGroups(): Result<List<String>> {
        // TODO: Backend not implemented
        return Result.success(FAKE_GROUP_LIST)
    }
}

// FIXME: Delete when not needed
private val FAKE_GROUP_LIST = listOf("Group 1", "Group 2", "Group 3", "Group 4", "Group 5", "Group 6")

interface BackendRepository {
    suspend fun register(user: String, firebaseId: String): Result<Unit>
    suspend fun login(user: String, firebaseId: String): Result<Unit>
    suspend fun getMovies(): Result<List<Movie>>
    suspend fun getGroups(): Result<List<String>>
}
