package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.GroupException
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.usecases.UseCaseGroups
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import showProgressIndicator
import timber.log.Timber

@HiltViewModel
class TabGroupsViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val useCaseGroups: UseCaseGroups,
) : ViewModel() {

    private val _backendState = MutableStateFlow(TabBackendState())
    val backendState: StateFlow<TabBackendState> = _backendState.asStateFlow()

    private val _uiState = MutableStateFlow(TabUiState())
    val uiState: StateFlow<TabUiState> = _uiState.asStateFlow()

    init {
        refreshGroups()
    }

    fun refreshGroups() = viewModelScope.launch {
        useCaseGroups(Unit).let { result ->
            result.collectLatest { newState ->
                _backendState.update {
                    newState
                }
            }
        }
    }

    fun addGroup(groupName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _backendState.showProgressIndicator(true)

        repository.addGroups(groupName).fold(
            onSuccess = {
                refreshGroups()
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = CustomException.GenericException("New group created!"),
                        isLoading = false,
                    )
                }
            },
            onFailure = {
                Timber.e(it)
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = GroupException.AddGroup(),
                        isLoading = false,
                    )
                }
            },
        )
    }

    fun updatedMemberGroup(groupId: Int, email: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _backendState.showProgressIndicator(true)
        repository.addMemberGroup(groupId, email).fold(
            onSuccess = {
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = CustomException.GenericException("New Member added!"),
                        isLoading = false,
                    )
                }
            },
            onFailure = {
                Timber.d("Esta es la excepcion $it")
                Timber.e(it)
                _backendState.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        errorMessage = CustomException.GenericException(
                            it.message ?: "Error on Get Groups",
                        ),
                    )
                }
            },
        )
    }

    fun deleteGroup(groupId: Int) = viewModelScope.launch(dispatcherProvider.io()) {
        _backendState.showProgressIndicator(true)

        try {
            repository.deleteGroup(groupId)
            refreshGroups() // Refresh group after deletion
            _backendState.update { oldState ->
                oldState.copy(
                    errorMessage = CustomException.GenericException("Group deleted"),
                    isLoading = false,
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
            _backendState.update { oldState ->
                oldState.copy(
                    errorMessage = GroupException.DeleteGroup(),
                    isLoading = false,
                )
            }
        }
    }

    fun updateGroupName(groupId: Int, newName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        repository.updateGroupName(groupId, newName).getOrThrow().let {
            refreshGroups()
        }
    }

    fun selectGroupByPos(pos: Int) {
        _uiState.update { it.copy(selectedGroupPos = pos) }
    }

    fun clearErrorMessage() {
        _backendState.update { it.copy(errorMessage = null) }
    }
}
