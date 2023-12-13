package com.apptolast.familyfilmapp.ui.screens.recommend.states

import com.apptolast.familyfilmapp.model.local.GenreInfo

data class GenresBackendState(
    val genreInfo: List<GenreInfo>,
) {
    constructor() : this(
        genreInfo = emptyList(),
    )
}
