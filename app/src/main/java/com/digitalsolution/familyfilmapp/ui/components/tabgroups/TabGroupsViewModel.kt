package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TabGroupsViewModel @Inject constructor(
    private val repository: BackendRepository,
) : ViewModel() {

    private val _groups = MutableLiveData<List<GroupInfo>>(emptyList())
    val groups: LiveData<List<GroupInfo>> = _groups

    init {
        viewModelScope.launch {
            _groups.postValue(repository.getGroups().getOrElse {
                Timber.e(it)
                emptyList()
            })
        }
    }

    companion object {
        private val testGroups = listOf(
            GroupInfo(
                1,
                "Group Test",
                watchList = emptyList(),
                viewList = emptyList()
            )
        )
    }
}
