package com.digitalsolution.familyfilmapp.model.local

data class GroupInfo(
    val id: Int,
    val name: String,
    val watchList: List<Movie>,
    val viewList: List<Movie>,
) {
    constructor() : this(
        id = -1,
        name = "",
        watchList = emptyList<Movie>(),
        viewList = emptyList<Movie>(),
    )


}
