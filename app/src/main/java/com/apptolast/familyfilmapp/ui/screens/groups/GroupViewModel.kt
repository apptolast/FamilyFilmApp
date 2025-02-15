package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    val backendState: StateFlow<BackendState>
        field: MutableStateFlow<BackendState> = MutableStateFlow(BackendState())

    val uiState: StateFlow<UiState>
        field: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    init {
        viewModelScope.launch {
//            backendState.update { it.copy(isLoading = true) }
            awaitAll(
                async { getGroups() },
                async { me() },
            )
//            backendState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun getGroups() {
        repository.getMyGroups(auth.currentUser?.uid!!)
            .catch { error ->
                backendState.update {
                    it.copy(errorMessage = error.message)
                }
            }
            .first().let { groups ->
                backendState.update {
                    it.copy(
                        groups = groups.sortedWith(
                            compareBy(String.CASE_INSENSITIVE_ORDER) { group -> group.name },
                        ),
                        errorMessage = null,
                    )
                }
            }
    }

    private suspend fun me() {
        val authUser = auth.currentUser ?: return

        repository.getUserById(authUser.uid).collectLatest { user ->
            backendState.update {
                it.copy(currentUser = user)
            }
        }
    }

    fun createGroup(groupName: String) {
        repository.createGroup(viewModelScope, groupName)
    }

    fun changeGroupName(group: Group) {
        repository.updateGroup(viewModelScope, group)
    }

    fun deleteGroup(group: Group) {
        repository.deleteGroup(viewModelScope, group) {
            viewModelScope.launch { getGroups() }
        }
    }

    fun addMember(group: Group, email: String) {
        repository.addMember(viewModelScope, group, email)
    }

    fun deleteMember(group: Group, user: User) {
        repository.deleteMember(viewModelScope, group, user)
    }

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
