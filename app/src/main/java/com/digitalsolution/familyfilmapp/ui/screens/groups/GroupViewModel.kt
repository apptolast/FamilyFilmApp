package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.mutableStateOf
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

    fun addGroupMember(groupId: Int, email: String) =
        viewModelScope.launch(dispatcherProvider.io()) {
            _state.update { state ->
                state.copy(
                    addMemberInfoMessage = repository.addGroupMember(groupId, email).getOrElse {
                        Timber.e(it)
                        ""
                    },
                )
            }
            updateAddMemberUiState(
                AddMemberUiState().copy(
                    showSnackbar = mutableStateOf(true),
                ),
            )
        }

    fun updateUiState(newGroupUIState: GroupUiState) {
        _groupUIState.value = newGroupUIState
    }

    fun updateAddMemberUiState(newAddMemberUIState: AddMemberUiState) {
        _addMemberUIState.value = newAddMemberUIState
    }
}
