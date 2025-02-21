package com.apptolast.familyfilmapp.model.local

/**
 * Movies selected by a user
 */
data class SelectedMovie(val movieId: Int, val groupsIds: List<String>) {
    constructor() : this(
        movieId = 0,
        groupsIds = emptyList(),
    )
}
