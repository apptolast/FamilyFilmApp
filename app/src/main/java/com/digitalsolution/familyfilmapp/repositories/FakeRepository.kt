package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.local.FilmSearchData
import com.digitalsolution.familyfilmapp.model.local.GroupData
import javax.inject.Inject

class FakeRepositoryImpl @Inject constructor() : FilmRepository {

    override fun generateFakeFilmData(size: Int): List<FilmSearchData> {
        val films = mutableListOf<FilmSearchData>()

        for (i in 0 until size) {
            films.add(
                FilmSearchData(
                    img = "https://loremflickr.com/400/400/cat?lock=$i",
                    title = "Película $i"
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
    fun generateFakeFilmData(size: Int): List<FilmSearchData>
    fun generateGroups(size: Int): List<GroupData>
}

