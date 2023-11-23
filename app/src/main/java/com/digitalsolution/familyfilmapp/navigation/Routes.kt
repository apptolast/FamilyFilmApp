package com.digitalsolution.familyfilmapp.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.ui.graphics.vector.ImageVector
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.digitalsolution.familyfilmapp.navigation.navtypes.GroupsNavType
import com.digitalsolution.familyfilmapp.navigation.navtypes.HomeNavType
import com.digitalsolution.familyfilmapp.navigation.navtypes.LoginNavType
import com.digitalsolution.familyfilmapp.navigation.navtypes.ProfileNavType
import com.digitalsolution.familyfilmapp.navigation.navtypes.RecommendNavType
import com.digitalsolution.familyfilmapp.navigation.navtypes.SearchNavType
import com.digitalsolution.familyfilmapp.navigation.navtypes.route

sealed class Routes(
    @StringRes val title: Int,
    val routes: String,
    val icon: ImageVector?,
) {

    data object Login : Routes(
        title = R.string.screen_title_login,
        routes = LoginNavType.route,
        icon = null,
    )

    data object Home : Routes(
        title = R.string.screen_title_home,
        routes = HomeNavType.route,
        icon = Icons.Outlined.Home,
    )

    data object Recommend : Routes(
        title = R.string.screen_title_recommend,
        routes = RecommendNavType.route,
        icon = Icons.Outlined.Recommend,
    )

    data object Groups : Routes(
        title = R.string.screen_title_groups,
        routes = GroupsNavType.route,
        icon = Icons.Outlined.Groups,
    )

    data object Profile : Routes(
        title = R.string.screen_title_profile,
        routes = ProfileNavType.route,
        icon = Icons.Outlined.Person,
    )

    data object Search : Routes(
        title = R.string.screen_title_search,
        routes = SearchNavType.route,
        null,
    )

    data object Details : Routes(
        title = R.string.screen_title_details,
        routes = DetailNavTypeDestination.route,
        null,
    )
}
