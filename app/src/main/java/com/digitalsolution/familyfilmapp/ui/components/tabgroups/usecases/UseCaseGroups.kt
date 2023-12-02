package com.digitalsolution.familyfilmapp.ui.components.tabgroups.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TabBackendState
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class UseCaseGroups @Inject constructor(
    private val repository: BackendRepository,
) : BaseUseCase<Unit, Flow<TabBackendState>>() {
    override suspend fun execute(parameters: Unit): Flow<TabBackendState> = channelFlow {
        send(
            TabBackendState().copy(
                isLoading = true,
            ),
        )

        repository.getGroups().fold(
            onSuccess = { groups ->
                if (groups.isEmpty()) {
                    send(
                        TabBackendState().copy(
                            groups = generateFakeGroups(8),
                            isFakeList = true,
                            isLoading = false,
                            errorMessage = null,
                        ),
                    )
                } else {
                    send(
                        TabBackendState().copy(
                            groups = groups.sortedWith(
                                compareBy(String.CASE_INSENSITIVE_ORDER) { group ->
                                    group.name
                                },
                            ),
                            isFakeList = false,
                            isLoading = false,
                            errorMessage = null,
                        ),
                    )
                }
            },
            onFailure = {
                send(
                    TabBackendState().copy(
                        isLoading = false,
                        errorMessage = CustomException.GenericException(
                            it.message ?: "Error on Get Groups",
                        ),
                    ),
                )
            },
        )
    }
}

fun generateFakeGroups(count: Int): List<Group> {
    return List(count) { index ->
        Group(
            id = index,
            name = "Grupo Fake $index",
            groupCreatorId = (index + 1) * 10,
            watchList = generateFakeMovies(5),
            viewList = generateFakeMovies(3),
        )
    }
}

private fun generateFakeMovies(count: Int): List<Movie> {
    return List(count) { index ->
        Movie(
            title = "Movie fake $index",
            isAdult = true,
            genres = emptyList<Pair<Int, String>>(),
            image = "",
            synopsis = "",
            voteAverage = 0f,
            voteCount = 0,
            releaseDate = Calendar.getInstance().time,
            language = "",
        )
    }
}
