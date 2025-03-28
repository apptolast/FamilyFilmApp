package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.AdaptiveBanner
import com.apptolast.familyfilmapp.ui.screens.detail.MovieDetailScreen
import com.apptolast.familyfilmapp.ui.screens.groups.GroupsScreen
import com.apptolast.familyfilmapp.ui.screens.home.HomeScreen
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileScreen
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) {
            Routes.Home.routes
        } else {
            Routes.Login.routes
        },
    ) {
        composable(route = Routes.Login.routes) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
            )
        }
        composable(
            route = Routes.Home.routes,
            arguments = listOf(),
        ) {
            AddBanner {
                HomeScreen(
                    onClickNav = { route ->
                        navController.navigate(route)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        composable(route = Routes.Groups.routes) {
            AddBanner {
                GroupsScreen(
                    onClickNav = { route ->
                        navController.navigate(route)
                    },
                    onBack = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        composable(route = Routes.Profile.routes) {
            AddBanner {
                ProfileScreen(
                    viewModel = authViewModel,
                    onClickNav = { route ->
                        navController.navigate(route)
                    },
                    onBack = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        composable(
            route = Routes.Details.routes,
            arguments = DetailNavTypeDestination.argumentList,
        ) { backStackEntry ->
            val (movie) = DetailNavTypeDestination.parseArguments(backStackEntry)
            AddBanner {
                MovieDetailScreen(
                    movie = movie,
                    onBack = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun AddBanner(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        content()
        AdaptiveBanner()
    }
}
