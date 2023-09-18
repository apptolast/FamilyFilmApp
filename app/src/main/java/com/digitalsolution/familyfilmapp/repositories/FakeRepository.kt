package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.model.local.FilmSearchData
import javax.inject.Inject

class FakeRepositoryImpl @Inject constructor() : FakeRepository {

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


}


interface FakeRepository {
    fun generateFakeFilmData(size: Int): List<FilmSearchData>
}

