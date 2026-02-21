package com.apptolast.familyfilmapp.ui.screens.home

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.apptolast.familyfilmapp.MainDispatcherRule
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcher = MainDispatcherRule()

    @MockK
    lateinit var repository: Repository

    @RelaxedMockK
    lateinit var auth: FirebaseAuth

    @Before
    fun setUp() = runTest {
        val actualUserId = "userId"
        val expectedUser = User().copy(id = actualUserId, email = "a@a.com")

        coEvery { auth.uid } returns actualUserId
        coEvery { repository.getUserById(actualUserId) } returns flow { expectedUser }
        coEvery { repository.getPopularMovies() } returns flowOf(
            PagingData.from(
                listOf(
                    Movie().copy(
                        title = "Matrix",
                        overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                        """.trimIndent(),
                        posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                    ),
                ),
                sourceLoadStates = LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
            ),
        )

        viewModel = HomeViewModel(
            repository,
            auth,
            dispatcher.testDispatcherProvider,
        )
    }

    @Test
    fun `searchMovieByName should return the movie when it is contained in the list`() =
        runTest(dispatcher.testDispatcherProvider.io()) {
            val actualMovieName = "Matrix"
            val expectedMovies = listOf(Movie().copy(title = actualMovieName))

            coEvery { repository.searchTmdbMovieByName(actualMovieName) } returns Result.success(expectedMovies)

            viewModel.searchMovieByName(actualMovieName)

            assertThat(viewModel.homeUiState.value.filterMovies).isEqualTo(expectedMovies)
        }

    @Test
    fun `searchMovieByName should return EMPTY LIST when filter is EMPTY`() = runTest {
        val actualMovieName = ""

        viewModel.searchMovieByName(actualMovieName)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.homeUiState.value.filterMovies).isEqualTo(emptyList<Movie>())
        assertThat(viewModel.homeUiState.value.errorMessage).isEqualTo(CustomException.GenericException(null))
    }

    @Test
    fun `clearError should clear the state`() = runTest(dispatcher.testDispatcherProvider.io()) {
        viewModel.clearError()

        assertThat(viewModel.homeUiState.value.errorMessage).isEqualTo(
            CustomException.GenericException(null),
        )
    }
}
