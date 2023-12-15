package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.model.local.AddGroup
import com.apptolast.familyfilmapp.model.local.GenreInfo
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.UpdateGroupName
import com.apptolast.familyfilmapp.model.local.sealed.StatusResponse
import com.apptolast.familyfilmapp.model.mapper.AddGroupsMapper.toDomain
import com.apptolast.familyfilmapp.model.mapper.AddMemberMapper.toAddMemberBody
import com.apptolast.familyfilmapp.model.mapper.GenreMapper.toDomain
import com.apptolast.familyfilmapp.model.mapper.GroupInfoMapper.toDomain
import com.apptolast.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.apptolast.familyfilmapp.model.mapper.UpdateGroupNameMapper.toDomain
import com.apptolast.familyfilmapp.model.remote.request.AddMovieWatchListBody
import com.apptolast.familyfilmapp.model.remote.request.LoginBody
import com.apptolast.familyfilmapp.model.remote.request.RegisterBody
import com.apptolast.familyfilmapp.model.remote.request.RemoveMemberBody
import com.apptolast.familyfilmapp.network.BackendApi
import javax.inject.Inject
import com.apptolast.familyfilmapp.model.mapper.AddGroupsMapper.toBody as addGroupToBody
import com.apptolast.familyfilmapp.model.mapper.UpdateGroupNameMapper.toBody as updateGroupToBody

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

    override suspend fun getGroups(): Result<List<Group>> = kotlin.runCatching {
        backendApi.getGroups().data?.map {
            it.toDomain()
        } ?: emptyList()
    }

    override suspend fun getGenres(): Result<List<GenreInfo>> = kotlin.runCatching {
        backendApi.getGenres().data?.map {
            it.toDomain()
        } ?: emptyList()
    }

    override suspend fun addGroups(groupName: String): Result<AddGroup> = kotlin.runCatching {
        backendApi.addGroups(groupName.addGroupToBody()).data?.toDomain() ?: AddGroup()
    }

    override suspend fun deleteGroup(groupId: Int): Result<Unit> = kotlin.runCatching {
        backendApi.deleteGroup(groupId)
    }

    override suspend fun updateGroupName(groupId: Int, groupName: String): Result<UpdateGroupName> =
        kotlin.runCatching {
            backendApi.updateGroupName(groupId, groupName.updateGroupToBody()).data?.toDomain() ?: UpdateGroupName()
        }

    override suspend fun addMemberGroup(groupId: Int, emailUser: String): Result<Unit> = kotlin.runCatching {
        backendApi.addMemberGroup(groupId, emailUser.toAddMemberBody())
    }

    override suspend fun removeMemberGroup(groupId: Int, userId: Int): Result<Unit> = kotlin.runCatching {
        backendApi.removeMemberFromGroup(groupId, RemoveMemberBody(userId = userId))
    }

    override suspend fun addMovieToWatchList(groupId: Int, movieId: Int): Result<Unit> = kotlin.runCatching {
        backendApi.addMovieToWatchList(groupId, AddMovieWatchListBody(movieId = movieId))
    }

    override suspend fun addMovieToSeenList(groupId: Int, movieId: Int): Result<Unit> = kotlin.runCatching {
        backendApi.addMovieToSeenList(groupId, AddMovieWatchListBody(movieId = movieId))
    }
}

interface BackendRepository {
    suspend fun register(user: String, firebaseId: String): Result<Unit>
    suspend fun login(user: String, firebaseId: String): Result<Unit>
    suspend fun getMovies(): Result<List<Movie>>
    suspend fun getGroups(): Result<List<Group>>
    suspend fun getGenres(): Result<List<GenreInfo>>
    suspend fun addGroups(groupName: String): Result<AddGroup>
    suspend fun deleteGroup(groupId: Int): Result<Unit>
    suspend fun updateGroupName(groupId: Int, groupName: String): Result<UpdateGroupName>
    suspend fun addMemberGroup(groupId: Int, emailUser: String): Result<Unit>
    suspend fun removeMemberGroup(groupId: Int, userId: Int): Result<Unit>
    suspend fun addMovieToWatchList(groupId: Int, movieId: Int): Result<Unit>
    suspend fun addMovieToSeenList(groupId: Int, movieId: Int): Result<Unit>
}
