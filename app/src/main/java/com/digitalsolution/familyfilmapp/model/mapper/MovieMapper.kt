package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.remote.response.MovieWrapper

object MovieMapper {

    fun MovieWrapper.toDomain(): List<Movie> {
        val movies = arrayListOf<Movie>()
//        this.data?.map {
//
//        }
        return movies
    }

}