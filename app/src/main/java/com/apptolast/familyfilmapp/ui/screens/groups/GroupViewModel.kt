package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.extensions.updateModificationDate
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val backendState: StateFlow<BackendState>
        field: MutableStateFlow<BackendState> = MutableStateFlow(BackendState())

    val uiState: StateFlow<UiState>
        field: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    init {
        viewModelScope.launch {
            awaitAll(
                async { getGroups() },
                async { me() },
            )
        }
    }

    private suspend fun getGroups() {
        repository.getMyGroups(auth.currentUser?.uid!!).collectLatest { groups ->
            backendState.update {
                it.copy(
                    groups = groups.sortedWith(
                        compareBy(String.CASE_INSENSITIVE_ORDER) { group -> group.name },
                    ),
                    isLoading = false,
                    errorMessage = null,
                )
            }
        }
    }

    private suspend fun me() {
        val authUser = auth.currentUser ?: return
        repository.getUserById(authUser.uid).first().let { user ->
            backendState.update {
                it.copy(
                    currentUser = user,
                )
            }
        }
//        _backendState.update { it.copy(isLoading = true) }
//        repository.me().fold(
//            onSuccess = { user ->
//                _backendState.update {
//                    it.copy(
//                        userOwner = user,
//                        isLoading = false,
//                        errorMessage = null,
//                    )
//                }
//            },
//            onFailure = { error ->
//                Timber.e(error, "Error getting user info")
//                _backendState.update {
//                    it.copy(
//                        isLoading = false,
//                        errorMessage = error.message,
//                    )
//                }
//            },
//        )
    }

    fun createGroup(groupName: String) {
        repository.createGroup(viewModelScope, groupName)

//        _backendState.update { it.copy(isLoading = true) }
//
//        repository.addGroup(groupName).fold(
//            onSuccess = { groups ->
//                _backendState.update {
//                    it.copy(
//                        groups = groups,
//                        errorMessage = null,
//                        isLoading = false,
//                    )
//                }
//                _uiState.update {
//                    it.copy(
//                        showDialog = GroupScreenDialogs.None,
//                        selectedGroupIndex = it.selectedGroupIndex.inc(),
//                    )
//                }
//            },
//            onFailure = {
//                Timber.e(it)
//                _backendState.update { oldState ->
//                    oldState.copy(
//                        errorMessage = GroupException.AddGroup().value,
//                        isLoading = false,
//                    )
//                }
//            },
//        )
    }

    fun changeGroupName(group: Group) = repository.updateGroup(viewModelScope, group.updateModificationDate())

    fun deleteGroup(group: Group) {
        uiState.update {
            it.copy(
                selectedGroupIndex = (uiState.value.selectedGroupIndex - 1).coerceIn(
                    0,
                    Int.MAX_VALUE,
                ),
            )
        }
        repository.deleteGroup(viewModelScope, group.updateModificationDate())
    }

    fun addMember(group: Group, email: String) =
        repository.addMember(viewModelScope, group.updateModificationDate(), email)

    fun deleteMember(group: Group, user: User) =
        repository.deleteMember(viewModelScope, group.updateModificationDate(), user)

    fun showDialog(dialog: GroupScreenDialogs) = uiState.update { it.copy(showDialog = dialog) }

    fun selectGroup(index: Int) = viewModelScope.launch {
        uiState.update {
            it.copy(selectedGroupIndex = index)
        }
    }

    fun clearErrorMessage() = backendState.update { it.copy(errorMessage = null) }

    data class BackendState(
        val currentUser: User,
        val groups: List<Group>,
        val isLoading: Boolean,
        val errorMessage: String?,
    ) {
        constructor() : this(
            currentUser = User(),
            groups = emptyList(),
            isLoading = false,
            errorMessage = null,
        )
    }

    data class UiState(val showDialog: GroupScreenDialogs, val selectedGroupIndex: Int) {
        constructor() : this(
            showDialog = GroupScreenDialogs.None,
            selectedGroupIndex = 0,
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
