package com.apptolast.familyfilmapp.network

object TmdbConfig {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

    const val POSTER_GRID = "${IMAGE_BASE_URL}w500/"
    const val POSTER_DETAIL = "${IMAGE_BASE_URL}w780/"
    const val LOGO = "${IMAGE_BASE_URL}w185/"
    const val PLACEHOLDER_URL = "https://picsum.photos/133/200"
}
