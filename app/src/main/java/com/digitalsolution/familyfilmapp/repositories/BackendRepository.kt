package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.local.GenreInfo
import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.local.sealed.StatusResponse
import com.digitalsolution.familyfilmapp.model.mapper.GroupInfoMapper.toDomain
import com.digitalsolution.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.digitalsolution.familyfilmapp.model.remote.request.LoginBody
import com.digitalsolution.familyfilmapp.model.remote.request.RegisterBody
import com.digitalsolution.familyfilmapp.network.BackendApi
import javax.inject.Inject

class BackendRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val localRepository: LocalRepository,
) : BackendRepository {

    override suspend fun register(user: String, firebaseId: String): Result<Unit> = kotlin.runCatching {
        // Create the body object
        val body = RegisterBody(user, firebaseId)
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
        backendApi.getMovies().data?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getGroups(): Result<List<GroupInfo>> = kotlin.runCatching {
        backendApi.getGroups().data?.map {
            it.toDomain()
        } ?: emptyList()
    }

    override suspend fun getGenres(): Result<List<GenreInfo>> {
        TODO("Not yet implemented")
    }
}

interface BackendRepository {
    suspend fun register(user: String, firebaseId: String): Result<Unit>
    suspend fun login(user: String, firebaseId: String): Result<Unit>
    suspend fun getMovies(): Result<List<Movie>>
    suspend fun getGroups(): Result<List<GroupInfo>>
    suspend fun getGenres(): Result<List<GenreInfo>>
}
