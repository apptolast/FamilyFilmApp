package com.digitalsolution.familyfilmapp.ui.screens.recommend.states

import com.digitalsolution.familyfilmapp.model.local.GenreInfo

data class GenresBackendState(
    val genreInfo: List<GenreInfo>,
) {
    constructor() : this(
        genreInfo = emptyList(),
    )
}
