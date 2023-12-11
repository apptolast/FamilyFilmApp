package com.digitalsolution.familyfilmapp.model.local

data class Group(
    val id: Int,
    val name: String,
    val groupCreatorId: Int,
    val watchList: List<Movie>,
    val viewList: List<Movie>,
    val users: List<Users>,
) {
    constructor() : this(
        id = -1,
        name = "",
        groupCreatorId = -1,
        watchList = emptyList<Movie>(),
        viewList = emptyList<Movie>(),
        users = emptyList<Users>(),
    )
}
