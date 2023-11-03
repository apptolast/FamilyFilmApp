package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routes(
    val routes: String,
    val icon: ImageVector?,
) {

    data object Login : Routes(
        routes = "Login",
        icon = null,
    )

    data object Home : Routes(
        "Home",
        icon = Icons.Outlined.Home,
    )

    data object Recommend : Routes(
        "Recommend",
        icon = Icons.Outlined.Recommend,
    )

    data object Groups : Routes(
        "Groups",
        icon = Icons.Outlined.Groups,
    )

    data object Profile : Routes(
        "Profile",
        icon = Icons.Outlined.Person,
    )

    data object Details : Routes(
        "Details",
        icon = null,
    )

    data object Search : Routes(
        "Search",
        null,
    )
}
