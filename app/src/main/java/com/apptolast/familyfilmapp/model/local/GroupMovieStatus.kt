package com.apptolast.familyfilmapp.model.local

import com.apptolast.familyfilmapp.model.local.types.MovieStatus

data class GroupMovieStatus(
    val groupId: String,
    val userId: String,
    val movieId: Int,
    val status: MovieStatus,
)
