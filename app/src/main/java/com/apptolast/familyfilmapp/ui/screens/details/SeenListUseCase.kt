package com.apptolast.familyfilmapp.ui.screens.details

import com.apptolast.familyfilmapp.BaseUseCase
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class SeenListUseCase @Inject constructor(
    private val repository: BackendRepository,
) : BaseUseCase<Pair<Int, Int>, Flow<DetailScreenUIState>>() {

    override suspend fun execute(parameters: Pair<Int, Int>): Flow<DetailScreenUIState> = channelFlow {
        val (groupId, movieId) = parameters

        send(
            DetailScreenUIState().copy(
                isLoading = true,
            ),
        )

        repository.addMovieToSeenList(groupId, movieId).fold(
            onSuccess = {
                send(
                    DetailScreenUIState().copy(
                        successMovieToWatchList = "Success Movie Added",
                        isLoading = false,
                        errorMessage = null,
                    ),
                )
            },
            onFailure = {
                send(
                    DetailScreenUIState().copy(
                        successMovieToWatchList = "",
                        isLoading = false,
                        errorMessage = CustomException.GenericException(
                            it.message ?: "Error",
                        ),
                    ),
                )
            },
        )
    }
}
