package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.screens.groups.showProgressIndicator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TabGroupsViewModel @Inject constructor(
    private val repository: BackendRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TabBackendState())
    val state: StateFlow<TabBackendState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TabBackendState(),
    )


    private val _tabUIState = MutableLiveData(TabUIState())
    val tabUIState: LiveData<TabUIState> = _tabUIState

    init {
        refreshGroups()
        indexOfSelectedGroup(0)
    }

    fun refreshGroups() = viewModelScope.launch {
        _state.showProgressIndicator(true)
        _state.update { oldState ->
            oldState.copy(
                groups = repository.getGroups().getOrElse {
                    Timber.e(it)
                    emptyList()
                }.sortedWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { group ->
                        group.name
                    },
                ),
                isLoading = false,
            )
        }
    }

    fun indexOfSelectedGroup(index: Int) {
        _tabUIState.value = _tabUIState.value?.copy(selectedGroup = index) ?: TabUIState()
    }


}
