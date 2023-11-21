package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.repositories.LocalRepository
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupUiState())
    val uiState: StateFlow<GroupUiState> = _uiState

    fun updateSelectedGroup(group: Group) {
        _uiState.update {
            it.copy(
                deleteGroupButtonVisibility = group.groupCreatorId == localRepository.getUserId(),
            )
        }
    }

}
