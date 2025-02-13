package com.apptolast.familyfilmapp.model.local

data class User(
    val id: String,
    val email: String,
    val language: String,
//    val groupIds: List<String>,
    val watched: List<SelectedMovie>,
    val toWatch: List<SelectedMovie>,
) {
    constructor() : this(
        id = "",
        email = "",
        language = "",
//        groupIds = emptyList<String>(),
        watched = emptyList(),
        toWatch = emptyList(),
    )
}
