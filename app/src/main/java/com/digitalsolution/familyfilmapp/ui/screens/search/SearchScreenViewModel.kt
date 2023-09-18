package com.digitalsolution.familyfilmapp.ui.screens.search

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.model.local.FilmSearchData
import com.digitalsolution.familyfilmapp.repositories.FakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val fakeRepository: FakeRepository
) : ViewModel() {

    fun getListFilmFake(): List<FilmSearchData> = fakeRepository.generateFakeFilmData(20)
}
