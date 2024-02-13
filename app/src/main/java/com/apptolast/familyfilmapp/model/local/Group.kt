package com.apptolast.familyfilmapp.model.local

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


val FAKE_GROUPS = listOf(
    Group(
        id = 1,
        name = "Group 1",
        groupCreatorId = 1,
        watchList = FAKE_MOVIES_WATCHLIST,
        viewList = FAKE_MOVIES_VIEWLIST,
        users = FAKE_USERS
    )
)
