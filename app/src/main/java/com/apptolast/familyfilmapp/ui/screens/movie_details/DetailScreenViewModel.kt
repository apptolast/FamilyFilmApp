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

    val state: StateFlow<State>
        field : MutableStateFlow<State> = MutableStateFlow(State())


    fun displayDialog(movieId: Int, dialogType: DialogType) = viewModelScope.launch {
        repository.getDetailsMovieDialog(movieId).getOrNull()?.let { details ->
            state.update {
                it.copy(
                    movieId = movieId,
                    dialogType = dialogType,
                    dialogGroupList = details.groups,
                )
            }
        }
    }


    data class State(
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
}
