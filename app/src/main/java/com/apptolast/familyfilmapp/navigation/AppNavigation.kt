package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.screens.DetailsScreen
import com.apptolast.familyfilmapp.ui.screens.groups.GroupsScreen
import com.apptolast.familyfilmapp.ui.screens.home.HomeScreen
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileScreen
import com.apptolast.familyfilmapp.ui.screens.recommend.RecommendScreen
import com.apptolast.familyfilmapp.ui.screens.search.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        modifier = Modifier.padding(),
        startDestination = Routes.Login.routes,
    ) {
        composable(route = Routes.Login.routes) {
            LoginScreen(navController = navController)
        }
        composable(
            route = Routes.Home.routes,
            arguments = listOf(),
        ) {
            HomeScreen(navController = navController)
        }
        composable(route = Routes.Recommend.routes) {
            RecommendScreen(navController = navController)
        }
        composable(route = Routes.Groups.routes) {
            GroupsScreen(navController = navController)
        }
        composable(route = Routes.Profile.routes) {
            ProfileScreen(
                navController = navController,
            )
        }
        composable(
            route = Routes.Details.routes,
            arguments = DetailNavTypeDestination.argumentList,
        ) { backStackEntry ->
            val (movie, groupId) = DetailNavTypeDestination.parseArguments(backStackEntry)
            if (groupId != null) {
                DetailsScreen(
                    navController = navController,
                    movie = movie,
                    groupId = groupId,
                )
            }
        }
        composable(route = Routes.Search.routes) {
            SearchScreen(navController = navController)
        }
    }
}
