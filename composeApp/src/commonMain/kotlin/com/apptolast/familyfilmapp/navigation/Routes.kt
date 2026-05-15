package com.apptolast.familyfilmapp.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Login : Routes

    @Serializable
    data object Home : Routes

    @Serializable
    data object Discover : Routes

    @Serializable
    data object Chat : Routes

    @Serializable
    data object Groups : Routes

    @Serializable
    data object Profile : Routes

    @Serializable
    data class Details(val mediaId: Int, val mediaType: String) : Routes
}
