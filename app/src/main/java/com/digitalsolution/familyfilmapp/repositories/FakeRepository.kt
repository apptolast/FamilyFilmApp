package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.local.GroupData
import com.digitalsolution.familyfilmapp.model.local.Movie
import javax.inject.Inject

class FakeRepositoryImpl @Inject constructor() : FilmRepository {
    override fun generateFakeCategoryData(): List<String> = listOf(
        "Terror",
        "Comedia",
        "Romántico",
        "Ciencia Ficción",
        "Fantasía",
        "Acción",
        "Drama",
        "Anime",
        "Aventura",
        "Musical"
    )

    override fun generateFakeFilmData(size: Int): List<Movie> {
        val films = mutableListOf<Movie>()

        for (i in 0 until size) {
            films.add(
                Movie(
                    title = "Película $i",
                    image = "https://loremflickr.com/400/400/cat?lock=$i",
                )
            )
        }

        return films
    }

    override fun generateGroups(size: Int): List<GroupData> {
        val groups = mutableListOf<GroupData>()

        for (i in 0 until size) {
            groups.add(
                GroupData(
                    name = "Grupo $i"
                )
            )
        }

        return groups
    }
}


interface FilmRepository {
    fun generateFakeCategoryData(): List<String>
    fun generateFakeFilmData(size: Int): List<Movie>
    fun generateGroups(size: Int): List<GroupData>
}

