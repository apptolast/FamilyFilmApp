package com.digitalsolution.familyfilmapp.ui.screens.home.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.repositories.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabGroupsViewModel @Inject constructor(
    private val repository: FilmRepository,
) : ViewModel() {

    private val _groups = MutableLiveData(arrayListOf<Group>())
    val groups: LiveData<ArrayList<Group>> = _groups

    init {
        viewModelScope.launch {
            _groups.value = repository.getGroups()
        }
    }
}
