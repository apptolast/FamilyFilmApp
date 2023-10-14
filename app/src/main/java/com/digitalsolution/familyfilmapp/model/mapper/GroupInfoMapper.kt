package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.digitalsolution.familyfilmapp.model.remote.response.GroupInfoRemote

object GroupInfoMapper {

    fun GroupInfoRemote.toDomain() = GroupInfo(
        id = id,
        name = name,
        watchList = watchList?.map {
            it.movieRemote?.toDomain()
        } ?: emptyList<Movie>(),
        viewList = viewList?.map {
            it.movieRemote?.toDomain()
        } ?: emptyList<Movie>()
    )
}
