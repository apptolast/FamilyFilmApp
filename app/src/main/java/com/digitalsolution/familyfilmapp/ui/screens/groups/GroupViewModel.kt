package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.repositories.LocalRepository
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val localRepository: LocalRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupUiState())
    val uiState: StateFlow<GroupUiState> = _uiState

    fun updateGroupName(groupId: Int, newName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        repository.updateGroupName(groupId, newName).getOrThrow(
            // Notify error throw the backendState (not created yet)
        ).let{
            // Update the group with the new name (re-sync)
        }
    }

    fun updateSelectedGroup(group: Group) {
        _uiState.update {
            it.copy(
                deleteGroupButtonVisibility = group.groupCreatorId == localRepository.getUserId(),
            )
        }
    }
}
