package com.digitalsolution.familyfilmapp.ui.screens.search

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.model.local.FilmSearchData
import com.digitalsolution.familyfilmapp.repositories.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val fakeRepository: FilmRepository
) : ViewModel() {

    fun getListFilmFake(): List<FilmSearchData> = fakeRepository.generateFakeFilmData(20)
}
