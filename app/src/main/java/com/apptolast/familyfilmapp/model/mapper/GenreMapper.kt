package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.Genre
import com.apptolast.familyfilmapp.model.remote.response.GenreRemote

object GenreMapper {

    fun GenreRemote.toDomain() = Genre(
        id = id ?: -1,
        name = name ?: "",
    )
}
