package com.apptolast.familyfilmapp.ui.screens.movieDetails

import com.apptolast.familyfilmapp.BaseUseCase
import com.apptolast.familyfilmapp.exceptions.GenericException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class SeenListUseCase @Inject constructor(private val repository: BackendRepository) :
    BaseUseCase<Pair<Int, Int>, Flow<DetailScreenStateState>>() {

    override suspend fun execute(parameters: Pair<Int, Int>): Flow<DetailScreenStateState> = channelFlow {
        val (groupId, movieId) = parameters

        send(
            DetailScreenStateState().copy(
                isLoading = true,
            ),
        )

        repository.addMovieToSeenList(groupId, movieId).fold(
            onSuccess = {
                send(
                    DetailScreenStateState().copy(
//                        successMovieToWatchList = "Success Movie Added",
                        isLoading = false,
                        errorMessage = null,
                    ),
                )
            },
            onFailure = {
                send(
                    DetailScreenStateState().copy(
//                        successMovieToWatchList = "",
                        isLoading = false,
                        errorMessage = GenericException(
                            it.message ?: "Error",
                        ),
                    ),
                )
            },
        )
    }
}
