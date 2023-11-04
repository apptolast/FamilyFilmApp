package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.digitalsolution.familyfilmapp.model.remote.response.GroupInfoRemote

object GroupInfoMapper {

    fun GroupInfoRemote.toDomain() = Group(
        id = id ?: -1,
        name = name ?: "",
        watchList = watchList?.map {
            it.movieRemote?.toDomain() ?: Movie()
        } ?: emptyList(),
        viewList = viewList?.map {
            it.movieRemote?.toDomain() ?: Movie()
        } ?: emptyList(),
    )
}
