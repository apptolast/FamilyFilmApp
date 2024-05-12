package com.apptolast.familyfilmapp.ui.screens.recommend.states

import com.apptolast.familyfilmapp.model.local.Genre

data class GenresBackendState(
    val genre: List<Genre>,
) {
    constructor() : this(
        genre = emptyList(),
    )
}
