package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.model.local.MemeberData
import com.digitalsolution.familyfilmapp.model.local.Movie
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

    override suspend fun getGroups(): ArrayList<Group> {
        val numElements = 5

        return arrayListOf<Group>().apply {
            for (i in 0 until numElements) {
                add(
                    Group(
                        image = "https://loremflickr.com/400/400/cat?lock=$i",
                        name = "Grupo $i"
                    )
                )
            }
        }
    }

    override fun generateMembersForGroups(size: Int): List<MemeberData> {
        val members = mutableListOf<MemeberData>()

        for (i in 0 until size) {
            members.add(
                MemeberData(
                    image = "https://loremflickr.com/400/400/cat?lock=$i",
                    name = "Member of group for example $i"
                )
            )
        }

        return members
    }
}

interface FilmRepository {
    fun generateFakeCategoryData(): List<String>
    fun generateFakeFilmData(size: Int): List<Movie>
    suspend fun getGroups(): ArrayList<Group>
    fun generateMembersForGroups(size: Int): List<MemeberData>
}
