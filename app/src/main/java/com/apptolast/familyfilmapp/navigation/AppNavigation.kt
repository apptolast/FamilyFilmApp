package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.navigation.navtypes.SearchNavTypeDestination
import com.apptolast.familyfilmapp.ui.screens.detail.DetailsScreenRoot
import com.apptolast.familyfilmapp.ui.screens.groups.GroupsScreen
import com.apptolast.familyfilmapp.ui.screens.home.HomeScreen
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileScreen
import com.apptolast.familyfilmapp.ui.screens.recommend.RecommendScreen
import com.apptolast.familyfilmapp.ui.screens.search.SearchScreen
import com.apptolast.familyfilmapp.ui.shared_viewmodel.AuthState
import com.apptolast.familyfilmapp.ui.shared_viewmodel.AuthViewModel

@Composable
fun AppNavigation(viewModel: AuthViewModel = hiltViewModel()) {

    val navController = rememberNavController()

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        modifier = Modifier.padding(),
        startDestination = if (authState is AuthState.Authenticated) {
            Routes.Home.routes
        } else {
            Routes.Login.routes
        },
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
            val (movie) = DetailNavTypeDestination.parseArguments(backStackEntry)
            DetailsScreenRoot(movie = movie)
        }
        composable(
            route = Routes.Search.routes,
            arguments = SearchNavTypeDestination.argumentList,
        ) { backStackEntry ->
            val (groupId) = SearchNavTypeDestination.parseArguments(backStackEntry)
            SearchScreen(
                navController = navController,
                groupId = groupId,
            )
        }
    }
}
