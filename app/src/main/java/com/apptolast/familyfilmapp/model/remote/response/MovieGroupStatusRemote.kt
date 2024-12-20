package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.GroupStatus
import com.apptolast.familyfilmapp.model.local.MovieGroupStatus
import com.apptolast.familyfilmapp.model.local.MovieStatus
import com.google.gson.annotations.SerializedName

class MovieGroupStatusRemote(
    @SerializedName("movieId") val movieId: Int,
    @SerializedName("groups") val groups: List<GroupStatusRemote>,
)

class GroupStatusRemote(
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("groupName") val groupName: String,
    @SerializedName("status") val status: String,
)

fun MovieGroupStatusRemote.toDomain() = MovieGroupStatus(movieId, groups.map { it.toDomain() })

fun GroupStatusRemote.toDomain() = GroupStatus(groupId, groupName, MovieStatus.fromString(status))
