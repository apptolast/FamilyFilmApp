package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class MovieWrapper(

    @field:SerializedName("movies")
    val movies: List<MoviesItem?>? = null,

    @field:SerializedName("genres")
    val genres: List<GenresItem?>? = null,

    @field:SerializedName("users")
    val users: List<UsersItem?>? = null
)

data class UsersItem(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("groups")
    val groups: List<GroupsItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("avatar")
    val avatar: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("groupId")
    val groupId: Int? = null,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("userId")
    val userId: Int? = null,

    @field:SerializedName("group")
    val group: Group? = null
)

data class GroupsItem(

    @field:SerializedName("viewList")
    val viewList: List<Any?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("watchList")
    val watchList: List<WatchListItem?>? = null,

    @field:SerializedName("users")
    val users: List<UsersItem?>? = null
)

data class WatchListItem(

    @field:SerializedName("movie")
    val movie: Movie? = null,

    @field:SerializedName("groupId")
    val groupId: Int? = null,

    @field:SerializedName("movieId")
    val movieId: Int? = null,

    @field:SerializedName("group")
    val group: Group? = null
)

data class User(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("avatar")
    val avatar: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null
)

data class Genre(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class GenresItem(

    @field:SerializedName("genreId")
    val genreId: Int? = null,

    @field:SerializedName("movie")
    val movie: Movie? = null,

    @field:SerializedName("genre")
    val genre: Genre? = null,

    @field:SerializedName("movieId")
    val movieId: Int? = null,

    @field:SerializedName("movies")
    val movies: List<MoviesItem?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class Movie(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("adult")
    val adult: Boolean? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("voteAverage")
    val voteAverage: Any? = null,

    @field:SerializedName("releaseDate")
    val releaseDate: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("synopsis")
    val synopsis: String? = null,

    @field:SerializedName("genreIds")
    val genreIds: List<Int?>? = null,

    @field:SerializedName("genres")
    val genres: List<GenresItem?>? = null,

    @field:SerializedName("voteCount")
    val voteCount: Int? = null,

    @field:SerializedName("watchLists")
    val watchLists: List<Any?>? = null,

    @field:SerializedName("viewLists")
    val viewLists: List<Any?>? = null
)

data class MoviesItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("voteAverage")
    val voteAverage: Any? = null,

    @field:SerializedName("releaseDate")
    val releaseDate: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("synopsis")
    val synopsis: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("genreIds")
    val genreIds: List<Int?>? = null,

    @field:SerializedName("genres")
    val genres: List<GenresItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("voteCount")
    val voteCount: Int? = null,

    @field:SerializedName("adult")
    val adult: Boolean? = null,

    @field:SerializedName("watchLists")
    val watchLists: List<Any?>? = null,

    @field:SerializedName("viewLists")
    val viewLists: List<Any?>? = null,

    @field:SerializedName("genreId")
    val genreId: Int? = null,

    @field:SerializedName("movie")
    val movie: Movie? = null,

    @field:SerializedName("genre")
    val genre: Genre? = null,

    @field:SerializedName("movieId")
    val movieId: Int? = null
)

data class Group(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
