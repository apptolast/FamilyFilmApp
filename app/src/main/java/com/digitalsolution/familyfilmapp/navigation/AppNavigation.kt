package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    val graph = remember(navController) {
        NavigationGraph(navController)
    }

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
        ) { backStackEntry ->
            HomeScreen(navController = navController) {
                graph.openDetailPage(
                    it.image,
                    it.title,
                    it.releaseDate.toString(),
                    it.voteAverage,
                    it.isAdult,
                    it.synopsis,
                )
            }
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
            route = DetailPageDestination.route,
            arguments = DetailPageDestination.argumentList,
        ) { backStackEntry ->
            val (image, title, date, voteAverage, isAdult, synopsis) = DetailPageDestination.parseArguments(
                backStackEntry,
            )
            DetailsScreen(
                navController = navController,
                title = title,
                image = image,
                synopsis = synopsis,
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
