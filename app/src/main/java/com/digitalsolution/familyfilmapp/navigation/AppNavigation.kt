package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalsolution.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.digitalsolution.familyfilmapp.ui.screens.DetailsScreen
import com.digitalsolution.familyfilmapp.ui.screens.groups.GroupsScreen
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeScreen
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreen
import com.digitalsolution.familyfilmapp.ui.screens.profile.ProfileScreen
import com.digitalsolution.familyfilmapp.ui.screens.recommend.RecommendScreen
import com.digitalsolution.familyfilmapp.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val iUIState by viewModel.navigationUIState.observeAsState()

    iUIState?.let {
        AppNavHost(navController = navController, isUserLoggedIn = it.isUserLoggedIn)
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    isUserLoggedIn: Boolean = false,
) {
    NavHost(
        navController = navController,
        modifier = Modifier.padding(),
        startDestination = if (isUserLoggedIn) Routes.Home.routes else Routes.Login.routes,
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
                onClickNavigateLogin = {
                    navController.navigate(Routes.Login.routes)
                },
            )
        }
        composable(
            route = DetailNavTypeDestination.route,
            arguments = DetailNavTypeDestination.argumentList,
        ) { backStackEntry ->
            val (movie) = DetailNavTypeDestination.parseArguments(backStackEntry)
            DetailsScreen(
                navController = navController,
                movie = movie,
            )
        }
        composable(route = Routes.Search.routes) {
            SearchScreen(navController = navController)
        }
    }
}
