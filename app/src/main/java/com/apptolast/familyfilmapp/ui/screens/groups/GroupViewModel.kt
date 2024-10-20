package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.GroupException
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _backendState = MutableStateFlow(BackendState())
    val backendState: StateFlow<BackendState> = _backendState.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            awaitAll(
                async { getGroups() },
                async { me() },
            )
        }
    }

    private suspend fun getGroups() {
        _backendState.update { it.copy(isLoading = true) }

        repository.getGroups().fold(
            onSuccess = { groups ->
                _backendState.update {
                    it.copy(
                        groups = groups,
//                            .sortedWith(
//                                compareBy(String.CASE_INSENSITIVE_ORDER) { group ->
//                                    group.name
//                                },
//                            ),
                        isLoading = false,
                        errorMessage = null,
                    )
                }

                _uiState.update {
                    it.copy(
                        selectedGroupIndex = if (groups.isEmpty()) -1 else 0,
                    )
                }
            },
            onFailure = { error ->
                _backendState.update {
                    it.copy(
                        errorMessage = error.message,
                        isLoading = false,
                    )
                }
            },
        )
    }

    private suspend fun me() {
        _backendState.update { it.copy(isLoading = true) }
        repository.me().fold(
            onSuccess = { user ->
                _backendState.update {
                    it.copy(
                        userOwner = user,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            },
            onFailure = { error ->
                Timber.e(error, "Error getting user info")
                _backendState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
            },
        )
    }

    fun createGroup(groupName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _backendState.update { it.copy(isLoading = true) }

        repository.addGroup(groupName).fold(
            onSuccess = { groups ->
                _backendState.update {
                    it.copy(
                        groups = groups,
                        errorMessage = null,
                        isLoading = false,
                    )
                }
                _uiState.update {
                    it.copy(
                        showDialog = GroupScreenDialogs.None,
                        selectedGroupIndex = it.selectedGroupIndex.inc(),
                    )
                }
            },
            onFailure = {
                Timber.e(it)
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = GroupException.AddGroup().error,
                        isLoading = false,
                    )
                }
            },
        )
    }

    fun deleteGroup(group: Group) = viewModelScope.launch(dispatcherProvider.io()) {
        _backendState.update { it.copy(isLoading = true) }

        repository.deleteGroup(group.id).fold(
            onSuccess = { groups ->
                _backendState.update {
                    it.copy(
                        groups = groups,
                        errorMessage = null,
                        isLoading = false,
                    )
                }
                _uiState.update {
                    it.copy(
                        showDialog = GroupScreenDialogs.None,
                        selectedGroupIndex = it.selectedGroupIndex.dec(),
                    )
                }
            },
            onFailure = {
                Timber.e(it)
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = GroupException.DeleteGroup().error,
                        isLoading = false,
                    )
                }
                _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
            },
        )
    }

    fun changeGroupName(groupId: Int, newGroupName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _backendState.update { it.copy(isLoading = true) }

        repository.updateGroupName(groupId, newGroupName).fold(
            onSuccess = { groups ->
                _backendState.update {
                    it.copy(
                        groups = groups,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
            },
            onFailure = { error ->
                Timber.e(error)
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = GroupException.UpdateGroupName().error,
                        isLoading = false,
                    )
                }
                _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
            },
        )
    }

    fun addMember(groupId: Int, email: String) = viewModelScope.launch {
        _backendState.update { it.copy(isLoading = true) }
        repository.addMember(groupId, email).fold(
            onSuccess = { groups ->
                _backendState.update {
                    it.copy(
                        groups = groups,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
            },
            onFailure = { error ->
                Timber.e(error)

                when (error) {
                    is retrofit2.HttpException -> {
                        when (error.code()) {
                            404 -> "User not found"
                            500 -> "Server Error"
                            else -> "Error adding member to group"
                        }
                    }

                    else -> {
                        GroupException.AddMember().error
                    }
                }.let { errorMessage ->
                    _backendState.update { oldState ->
                        oldState.copy(
                            errorMessage = errorMessage,
                            isLoading = false,
                        )
                    }
                    _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
                }
            },
        )
    }

    fun deleteMember(groupId: Int, userId: Int) = viewModelScope.launch {
        _backendState.update { it.copy(isLoading = true) }

        repository.deleteMember(groupId, userId).fold(
            onSuccess = { groups ->
                _backendState.update {
                    it.copy(
                        groups = groups,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
            },
            onFailure = { error ->
                Timber.e(error)
                _backendState.update { oldState ->
                    oldState.copy(
                        errorMessage = GroupException.DeleteUser().error,
                        isLoading = false,
                    )
                }
                _uiState.update { it.copy(showDialog = GroupScreenDialogs.None) }
            },
        )
    }

    fun showDialog(dialog: GroupScreenDialogs) = _uiState.update { it.copy(showDialog = dialog) }

    fun selectGroup(index: Int) = viewModelScope.launch {
        _uiState.update {
            it.copy(selectedGroupIndex = index)
        }
    }

    fun clearErrorMessage() = _backendState.update { it.copy(errorMessage = null) }

    data class BackendState(
        val userOwner: User,
        val groups: List<Group>,
        val isLoading: Boolean,
        val errorMessage: String?,
    ) {
        constructor() : this(
            userOwner = User(),
            groups = emptyList(),
            isLoading = false,
            errorMessage = null,
        )
    }

    data class UiState(val showDialog: GroupScreenDialogs, val selectedGroupIndex: Int) {
        constructor() : this(
            showDialog = GroupScreenDialogs.None,
            selectedGroupIndex = -1,
        )
    }

    sealed interface GroupScreenDialogs {
        data object CreateGroup : GroupScreenDialogs
        data class DeleteGroup(val group: Group) : GroupScreenDialogs
        data class ChangeGroupName(val group: Group) : GroupScreenDialogs
        data class AddMember(val group: Group) : GroupScreenDialogs
        data class DeleteMember(val group: Group, val user: User) : GroupScreenDialogs
        data object None : GroupScreenDialogs
    }
}
