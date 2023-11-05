package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.GroupException
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupBackendState
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
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

    init {
        init()
    }

    private fun init() = viewModelScope.launch(dispatcherProvider.io()) {
        _state.showProgressIndicator(true)

        _state.update { state ->
            state.copy(
                groups = repository.getGroups().getOrElse {
                    Timber.e(it)
                    emptyList()
                },
                isLoading = false,
            )
        }
    }

    fun addGroup(groupName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _state.showProgressIndicator(true)

        repository.addGroups(groupName).fold(
            onSuccess = {
                init()
                _state.update { oldState ->
                    oldState.copy(
                        errorMessage = CustomException.GenericException("New group created!"),
                        isLoading = false,
                    )
                }
            },
            onFailure = {
                Timber.e(it)
                _state.update { oldState ->
                    oldState.copy(
                        errorMessage = GroupException.AddGroup(),
                        isLoading = false,
                    )
                }
            },
        )
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : BaseUiState> MutableStateFlow<T>.showProgressIndicator(value: Boolean) {
    this.value = this.value.copyWithLoading(value) as T
}
