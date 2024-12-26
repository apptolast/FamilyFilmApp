package com.apptolast.familyfilmapp.ui.screens.movie_details

import com.apptolast.familyfilmapp.BaseUseCase
import com.apptolast.familyfilmapp.exceptions.GenericException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class WatchListUseCase @Inject constructor(private val repository: BackendRepository) :
    BaseUseCase<Pair<Int, Int>, Flow<DetailScreenStateState>>() {

    override suspend fun execute(parameters: Pair<Int, Int>): Flow<DetailScreenStateState> = channelFlow {
        val (groupId, movieId) = parameters

        send(
            DetailScreenStateState().copy(
                isLoading = true,
            ),
        )

        repository.addMovieToWatchList(groupId, movieId).fold(
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
