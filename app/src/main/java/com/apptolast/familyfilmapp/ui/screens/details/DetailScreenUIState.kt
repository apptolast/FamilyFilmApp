package com.apptolast.familyfilmapp.ui.screens.details

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException

data class DetailScreenUIState(
    val successMovieToWatchList: String,
    val successMovieToViewList: String,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,

) : BaseUiState {

    constructor() : this(
        successMovieToWatchList = "",
        successMovieToViewList = "",
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState {
        TODO("Not yet implemented")
    }
}
