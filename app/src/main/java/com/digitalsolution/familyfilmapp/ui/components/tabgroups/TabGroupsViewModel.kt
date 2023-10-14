package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TabGroupsViewModel @Inject constructor(
    private val repository: BackendRepository,
) : ViewModel() {

//    private val _groups = MutableLiveData<List<GroupInfo>>(emptyList())
//    val groups: LiveData<List<GroupInfo>> = _groups

    private val _groups = MutableStateFlow(emptyList<GroupInfo>())
    val groups: StateFlow<List<GroupInfo>> = _groups.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
//            _groups.postValue(repository.getGroups().getOrElse {
//                Timber.e(it)
//                emptyList()
//            })

            _groups.update {
                repository.getGroups().getOrElse {
                    Timber.e(it)
                    emptyList()
                }
            }
        }
    }
}
