package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.GroupException
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import showProgressIndicator
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TabGroupsViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _backendState = MutableStateFlow(TabBackendState())
    val backendState: StateFlow<TabBackendState> = _backendState

    private val _uiState = MutableStateFlow(TabUiState())
    val uiState: StateFlow<TabUiState> = _uiState

    init {
        refreshGroups()
    }

    fun refreshGroups() = viewModelScope.launch {
        _backendState.showProgressIndicator(true)
        val groups = repository.getGroups().getOrElse {
            Timber.e(it)
            emptyList()
        }.sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { group ->
                group.name
            },
        )

        _backendState.update {
            it.copy(
                groups = groups,
                isLoading = false,
            )
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

    fun selectGroupByPos(pos: Int) {
        _uiState.update { it.copy(selectedGroupPos = pos) }
    }

    fun clearErrorMessage() {
        _backendState.update { it.copy(errorMessage = null) }
    }
}
