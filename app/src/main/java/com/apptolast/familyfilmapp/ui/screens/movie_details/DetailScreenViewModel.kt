package com.apptolast.familyfilmapp.ui.screens.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.GroupStatus
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

    val state: StateFlow<DetailScreenStateState>
        field : MutableStateFlow<DetailScreenStateState> = MutableStateFlow(DetailScreenStateState())


    fun displayDialog(movieId: Int, dialogType: DialogType) = viewModelScope.launch {
        repository.getDetailsMovieDialog(movieId).getOrNull()?.let { movieGroupStatus ->
            state.update {
                it.copy(
                    movieId = movieId,
                    dialogType = dialogType,
                    dialogGroupList = movieGroupStatus.groups,
                )
            }
        }
    }

    fun updateGroup(movieId: Int, groupId: Int, isChecked: Boolean) = viewModelScope.launch {
        repository.addMovieToGroup(
            movieId = movieId,
            groupId = groupId,
            dialogType = state.value.dialogType == DialogType.TO_SEE,
            isChecked = isChecked,
        ).getOrNull()?.let { movieGroupStatus ->
            state.update {
                it.copy(
                    dialogGroupList = movieGroupStatus.groups,
                )
            }
        }
    }
}

data class DetailScreenStateState(
    val movieId: Int? = null,
    val dialogType: DialogType = DialogType.NONE,
    val dialogGroupList: List<GroupStatus> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: CustomException? = null,
)

enum class DialogType {
    TO_SEE,
    SEEN,
    NONE,
}
