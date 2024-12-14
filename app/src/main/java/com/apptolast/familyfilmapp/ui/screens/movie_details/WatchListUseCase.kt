package com.apptolast.familyfilmapp.ui.screens.movie_details

import com.apptolast.familyfilmapp.BaseUseCase
import com.apptolast.familyfilmapp.exceptions.GenericException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class WatchListUseCase @Inject constructor(private val repository: BackendRepository) :
    BaseUseCase<Pair<Int, Int>, Flow<DetailScreenViewModel.State>>() {

    override suspend fun execute(parameters: Pair<Int, Int>): Flow<DetailScreenViewModel.State> = channelFlow {
        val (groupId, movieId) = parameters

        send(
            DetailScreenViewModel.State().copy(
                isLoading = true,
            ),
        )

        repository.addMovieToWatchList(groupId, movieId).fold(
            onSuccess = {
                send(
                    DetailScreenViewModel.State().copy(
//                        successMovieToWatchList = "Success Movie Added",
                        isLoading = false,
                        errorMessage = null,
                    ),
                )
            },
            onFailure = {
                send(
                    DetailScreenViewModel.State().copy(
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
