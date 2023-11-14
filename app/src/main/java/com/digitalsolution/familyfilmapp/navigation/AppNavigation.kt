package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.DetailsScreen
import com.digitalsolution.familyfilmapp.ui.screens.groups.GroupsScreen
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeScreen
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreen
import com.digitalsolution.familyfilmapp.ui.screens.profile.ProfileScreen
import com.digitalsolution.familyfilmapp.ui.screens.recommend.RecommendScreen
import com.digitalsolution.familyfilmapp.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(viewModel: NavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        modifier = Modifier.padding(),
        startDestination = Routes.Login.routes,
    ) {
        composable(route = Routes.Login.routes) {
            LoginScreen(navController = navController)
        }
        composable(route = Routes.Home.routes) {
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
                onClickNavigateLogin = {
                    navController.navigate(Routes.Login.routes)
                },
            )
        }
        composable(
            route = "${Routes.Details.routes}/{title}/{image}/{synopsis}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("image") { type = NavType.StringType },
                navArgument("synopsis") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            DetailsScreen(
                navController = navController,
                title = backStackEntry.arguments?.getString("title") ?: "title",
                image = backStackEntry.arguments?.getString("image") ?: "image",
                synopsis = backStackEntry.arguments?.getString("synopsis") ?: "synopsis",
            )
        }
        composable(route = Routes.Search.routes) {
            SearchScreen(navController = navController)
        }
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        val isBottomBarVisible = when (destination.route) {
            Routes.Home.routes,
            Routes.Recommend.routes,
            Routes.Groups.routes,
            Routes.Profile.routes,
            -> {
                true
            }

            else -> {
                false
            }
        }
        val titleScreens = when (destination.route) {
            Routes.Home.routes -> R.string.screen_title_home
            Routes.Recommend.routes -> R.string.screen_title_recommendations
            Routes.Groups.routes -> R.string.screen_title_groups
            Routes.Profile.routes -> R.string.screen_title_profile
            Routes.Search.routes -> R.string.screen_title_search
            else -> null
        }
        val searchBottomVisible = destination.route == Routes.Home.routes
        val isTopBarVisible = destination.route == Routes.Search.routes
        NavigationUIState(
            isBottomBarVisible,
            searchBottomVisible,
            isTopBarVisible,
            titleScreens,
        ).let {
            viewModel.updateUIState(it)
        }
    }
}
