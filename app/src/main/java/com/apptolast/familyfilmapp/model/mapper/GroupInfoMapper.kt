package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.apptolast.familyfilmapp.model.remote.response.GroupInfoRemote
import com.apptolast.familyfilmapp.model.remote.response.toDomain

object GroupInfoMapper {

    fun GroupInfoRemote.toDomain() = Group(
        id = id ?: -1,
        name = name ?: "",
        groupCreatorId = groupCreatorId ?: -1,
        watchList = watchList?.map {
            it.movieRemote?.toDomain() ?: Movie()
        } ?: emptyList(),
        viewList = viewList?.map {
            it.movieRemote?.toDomain() ?: Movie()
        } ?: emptyList(),
        users = users?.map {
            it.toDomain()
        } ?: emptyList(),
    )
}
