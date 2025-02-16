package com.apptolast.familyfilmapp.model.local

/**
 * Movies selected by a user
 */
data class SelectedMovie(val movieId: Int, val groups: List<Group>) {
    constructor() : this(
        movieId = 0,
        groups = emptyList(),
    )
}
