package com.apptolast.familyfilmapp.ui.components.tabgroups.usecases

import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.ui.components.tabgroups.TabBackendState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class UseCaseGroups @Inject constructor(private val repository: BackendRepository) :
    com.apptolast.familyfilmapp.BaseUseCase<Unit, Flow<TabBackendState>>() {
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
                            groups = null,
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
