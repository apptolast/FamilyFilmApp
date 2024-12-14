package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.User
import com.google.gson.annotations.SerializedName
//
//data class UserResponseRemote(
//    @SerializedName("id")
//    val id: Int? = null,
//
//    @SerializedName("email")
//    val email: String? = null,
//
//    @SerializedName("language")
//    val language: String? = null,
//
//    @SerializedName("seenMovies")
//    val seenMovies: List<MovieNameRemote>? = null,
//
//    @SerializedName("toSeeMovies")
//    val toSeeMovies: List<MovieNameRemote>? = null,
//
//    @SerializedName("joinedGroupsIds")
//    val joinedGroupsIds: List<Int>? = null,
//)
//
//fun UserResponseRemote.toDomain() = User(
//    id = id ?: -1,
//    email = email ?: "",
//    language = language ?: "es",
//    seenMovies = seenMovies?.map{it.toDomain()} ?: emptyList(),
//    toSeeMovies = toSeeMovies?.map{it.toDomain()} ?: emptyList(),
//
//)
//
//data class User(
//    val id: Int,
//    val email: String,
//    val language: String,
//    val seenMovies: List<MovieName>,
//    val toSeeMovies: List<MovieName>,
//    val joinedGroupsIds: List<Int>,
//) {
//    constructor() : this(
//        id = -1,
//        email = "",
//        language = "",
//        seenMovies = emptyList(),
//        toSeeMovies = emptyList(),
//        joinedGroupsIds = emptyList(),
//    )
//}
//
//data class MovieName(
//    val id: Int,
//    val title: String,
//)
//
