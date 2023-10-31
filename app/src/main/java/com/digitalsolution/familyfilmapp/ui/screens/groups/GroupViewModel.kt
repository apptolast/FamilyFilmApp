package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupBackendState
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.ui.screens.groups.uistates.AddMemberUiState
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
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
class GroupViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(GroupBackendState())
    val state: StateFlow<GroupBackendState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GroupBackendState(),
    )

    private val _groupUIState = MutableLiveData(GroupUiState())
    val groupUIState: LiveData<GroupUiState> = _groupUIState

    private val _addMemberUIState = MutableLiveData(AddMemberUiState())
    val addMemberUIState: LiveData<AddMemberUiState> = _addMemberUIState

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            _state.update { state ->
                state.copy(
                    groupsInfo = repository.getGroups().getOrElse {
                        Timber.e(it)
                        emptyList()
                    },
                )
            }
        }
    }

    fun updateUiState(newGroupUIState: GroupUiState) {
        _groupUIState.value = newGroupUIState
    }
}
