package com.apptolast.familyfilmapp.navigation

import com.apptolast.familyfilmapp.model.local.types.MediaType
import kotlinx.serialization.Serializable

/**
 * Navigation destinations as `@Serializable` types so Jetpack Navigation
 * Compose Multiplatform 2.9+ can use them directly with `composable<Route>`
 * and `navController.navigate(Route)`. No more custom NavType wrappers — the
 * route payload IS the destination.
 *
 * Per-route UI metadata (title StringResource + bottom-bar icon) is
 * intentionally kept out of this file; it lives next to the BottomNavigationBar
 * in block 13 so this layer stays free of Compose imports.
 */
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
    data class Details(val mediaId: Int, val mediaType: MediaType) : Routes
}
