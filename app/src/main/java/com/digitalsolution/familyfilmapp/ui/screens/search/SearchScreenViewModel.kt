package com.digitalsolution.familyfilmapp.ui.screens.search

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.repositories.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val fakeRepository: FilmRepository
) : ViewModel() {

    fun getListFilmFake(): List<Movie> = fakeRepository.generateFakeFilmData(20)
}
