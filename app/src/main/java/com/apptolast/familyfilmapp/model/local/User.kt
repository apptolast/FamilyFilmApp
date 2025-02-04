package com.apptolast.familyfilmapp.model.local

data class User(
    val id: String,
    val groupIds: List<String>,
    val email: String,
    val language: String,
    val watched: List<SelectedMovie>,
    val toWatch: List<SelectedMovie>,
) {
    constructor() : this(
        id = "",
        groupIds = emptyList<String>(),
        email = "",
        language = "",
        watched = emptyList(),
        toWatch = emptyList(),
    )
}
