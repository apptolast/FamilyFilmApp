package com.apptolast.familyfilmapp.ui.screens.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.BackendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val repository: BackendRepository,
) : ViewModel() {

    val state: StateFlow<State>
        field : MutableStateFlow<State> = MutableStateFlow(State())

    init {
        viewModelScope.launch() {
            repository.me().getOrNull()?.let { user ->
                repository.getGroups().getOrNull()?.let { groups ->
                    val filteredGroups = groups.filter { group -> group.id in user.joinedGroupsIds }
                    val result = filteredGroups.map { it.id to it.name }
                    state.update {
                        it.copy(
                            user = user,
                            dialogGroupList = result
                        )
                    }
                }
            }
        }
    }

    fun displayDialog(dialogType: DialogType) {
        state.update {
            it.copy(dialogType = dialogType)
        }
    }


    data class State(
        val user: User? = null,
        val dialogType: DialogType = DialogType.NONE,
        val dialogGroupList: List<Pair<Int, String>> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: CustomException? = null,
    )

    enum class DialogType {
        TO_SEE,
        SEEN,
        NONE,
    }

}
