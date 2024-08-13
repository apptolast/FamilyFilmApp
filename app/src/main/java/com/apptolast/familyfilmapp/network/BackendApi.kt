package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.request.AddGroupBody
import com.apptolast.familyfilmapp.model.remote.request.AddMemberBody
import com.apptolast.familyfilmapp.model.remote.request.AddMovieWatchListBody
import com.apptolast.familyfilmapp.model.remote.request.RemoveMemberBody
import com.apptolast.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.apptolast.familyfilmapp.model.remote.response.AddMemberRemote
import com.apptolast.familyfilmapp.model.remote.response.GenreRemote
import com.apptolast.familyfilmapp.model.remote.response.GroupRemote
import com.apptolast.familyfilmapp.model.remote.response.MovieCatalogueRemote
import com.apptolast.familyfilmapp.model.remote.response.MovieRemote
import com.apptolast.familyfilmapp.model.remote.response.UserRemote
import com.apptolast.familyfilmapp.network.ApiRoutesParams.GROUP_ID_PARAM
import com.apptolast.familyfilmapp.network.ApiRoutesParams.MOVIE_ID_PARAM
import com.apptolast.familyfilmapp.network.ApiRoutesParams.MOVIE_NAME
import com.apptolast.familyfilmapp.network.ApiRoutesParams.PAGE_MOVIES
import com.apptolast.familyfilmapp.network.ApiRoutesParams.USER_ID_PARAM
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendApi {

    @GET(ApiRoutes.ME)
    suspend fun me(): UserRemote

    @POST(ApiRoutes.USER_CREATE)
    suspend fun createUser(): UserRemote

    @GET(ApiRoutes.MOVIES)
    suspend fun getMovies(): List<MovieRemote>

    @GET(ApiRoutes.GROUPS)
    suspend fun getGroups(): List<GroupRemote>

    @POST(ApiRoutes.CREATE_GROUP)
    suspend fun addGroup(@Body addGroupBody: AddGroupBody): List<GroupRemote>

    @DELETE(ApiRoutes.REMOVE_GROUP)
    suspend fun deleteGroup(@Path(GROUP_ID_PARAM) groupId: Int): List<GroupRemote>

    @PUT(ApiRoutes.EDIT_GROUP_NAME)
    suspend fun updateGroupName(
        @Path(GROUP_ID_PARAM) groupId: Int,
        @Body updateGroupNameBody: UpdateGroupNameBody,
    ): List<GroupRemote>

    @GET(ApiRoutes.MOVIES_CATALOGUE)
    suspend fun getMoviesCatalogue(@Path(PAGE_MOVIES) page: Int): List<MovieCatalogueRemote>

    @GET(ApiRoutes.MOVIES_SEARCH_NAME)
    suspend fun searchMovieByName(
        @Path(PAGE_MOVIES) page: Int,
        @Path(MOVIE_NAME) movieName: String,
    ): List<MovieCatalogueRemote>

    @PUT(ApiRoutes.ADD_MEMBER)
    suspend fun addMember(@Path(GROUP_ID_PARAM) groupId: Int, @Body addMemberBody: AddMemberBody): List<GroupRemote>

    @DELETE(ApiRoutes.REMOVE_MEMBER)
    suspend fun deleteMember(@Path(GROUP_ID_PARAM) groupId: Int, @Path(USER_ID_PARAM) userId: Int): List<GroupRemote>

    @PATCH(ApiRoutes.ADD_MEMBER)
    suspend fun addMemberGroup(@Path(GROUP_ID_PARAM) groupId: Int, @Body addMemberBody: AddMemberBody): AddMemberRemote

    @PATCH(ApiRoutes.REMOVE_MEMBER_FROM_GROUP)
    suspend fun removeMemberFromGroup(@Path(GROUP_ID_PARAM) groupId: Int, @Body removeMemberBody: RemoveMemberBody)

    @PUT(ApiRoutes.ADD_MOVIE_TO_WATCHLIST)
    suspend fun addMovieToWatchList(@Path(GROUP_ID_PARAM) groupId: Int, @Path(MOVIE_ID_PARAM) movieId: Int)

    @PATCH(ApiRoutes.ADD_MOVIE_TO_SEEN)
    suspend fun addMovieToSeenList(
        @Path(GROUP_ID_PARAM) groupId: Int,
        @Body addMovieSeenListBody: AddMovieWatchListBody,
    )

    @GET(ApiRoutes.GENRES)
    suspend fun getGenres(): List<GenreRemote>
}
