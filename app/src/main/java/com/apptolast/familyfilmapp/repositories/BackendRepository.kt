package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.model.local.Genre
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.mapper.AddGroupsMapper.toBody as addGroupToBody
import com.apptolast.familyfilmapp.model.mapper.AddMemberMapper.toAddMemberBody
import com.apptolast.familyfilmapp.model.mapper.GenreMapper.toDomain
import com.apptolast.familyfilmapp.model.remote.request.AddMemberBody
import com.apptolast.familyfilmapp.model.remote.request.AddMovieWatchListBody
import com.apptolast.familyfilmapp.model.remote.request.RemoveMemberBody
import com.apptolast.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.apptolast.familyfilmapp.model.remote.response.toDomain
import com.apptolast.familyfilmapp.network.BackendApi
import javax.inject.Inject

class BackendRepositoryImpl @Inject constructor(private val backendApi: BackendApi) : BackendRepository {

    override suspend fun me(): Result<User> = kotlin.runCatching {
        backendApi.me().toDomain()
    }

    override suspend fun createUser(): Result<User> = kotlin.runCatching {
        backendApi.createUser().toDomain()
    }

    override suspend fun getMovies(): Result<List<Movie>> = kotlin.runCatching {
        backendApi.getMovies().map { it.toDomain() }
    }

    override suspend fun getGroups(): Result<List<Group>> = kotlin.runCatching {
        backendApi.getGroups().map {
            it.toDomain()
        }
    }

    override suspend fun getGenres(): Result<List<Genre>> = kotlin.runCatching {
        backendApi.getGenres().map {
            it.toDomain()
        }
    }

    override suspend fun addGroup(groupName: String): Result<List<Group>> = kotlin.runCatching {
        backendApi.addGroup(groupName.addGroupToBody()).map { it.toDomain() }
    }

    override suspend fun deleteGroup(groupId: Int): Result<List<Group>> = kotlin.runCatching {
        backendApi.deleteGroup(groupId).map { it.toDomain() }
    }

    override suspend fun deleteMember(groupId: Int, userId: Int): Result<List<Group>> = kotlin.runCatching {
        backendApi.deleteMember(groupId, userId).map { it.toDomain() }
    }

    override suspend fun updateGroupName(groupId: Int, groupName: String): Result<List<Group>> = kotlin.runCatching {
        UpdateGroupNameBody(groupName).let { groupNameBody ->
            backendApi.updateGroupName(groupId, groupNameBody).map { it.toDomain() }
        }
    }

    override suspend fun addMember(groupId: Int, email: String): Result<List<Group>> = kotlin.runCatching {
        AddMemberBody(email).let { addMemberBody ->
            backendApi.addMember(groupId, addMemberBody).map { it.toDomain() }
        }
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
    suspend fun me(): Result<User>
    suspend fun createUser(): Result<User>
    suspend fun getMovies(): Result<List<Movie>>
    suspend fun getGroups(): Result<List<Group>>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun addGroup(groupName: String): Result<List<Group>>
    suspend fun deleteGroup(groupId: Int): Result<List<Group>>
    suspend fun addMember(groupId: Int, email: String): Result<List<Group>>
    suspend fun deleteMember(groupId: Int, userId: Int): Result<List<Group>>
    suspend fun updateGroupName(groupId: Int, groupName: String): Result<List<Group>>
    suspend fun addMemberGroup(groupId: Int, emailUser: String): Result<Unit>
    suspend fun removeMemberGroup(groupId: Int, userId: Int): Result<Unit>
    suspend fun addMovieToWatchList(groupId: Int, movieId: Int): Result<Unit>
    suspend fun addMovieToSeenList(groupId: Int, movieId: Int): Result<Unit>
}
