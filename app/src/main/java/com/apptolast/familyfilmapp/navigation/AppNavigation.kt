package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.Column
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

/**
 * Main navigation component for the app.
 * Implements a persistent banner that stays visible across authenticated screens.
 *
 * Banner visibility logic:
 * - Login/Register screens: NO banner (user not authenticated)
 * - SplashScreen: NO banner (handled by authState check)
 * - Home, Groups, Profile, Details: YES banner (user authenticated)
 *
 * The banner is positioned at the app level (outside NavHost) to prevent
 * recreation on navigation, ensuring smooth UX and efficient ad loading.
 */
@Composable
fun AppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = if (authState is AuthState.Authenticated) {
                Routes.Home.routes
            } else {
                Routes.Login.routes
            },
            modifier = Modifier.weight(1f),
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
                HomeScreen(
                    onClickNav = { route ->
                        navController.navigate(route)
                    },
                )
            }
            composable(route = Routes.Groups.routes) {
                GroupsScreen(
                    onClickNav = { route ->
                        navController.navigate(route)
                    },
                    onBack = { navController.navigateUp() },
                )
            }
            composable(route = Routes.Profile.routes) {
                ProfileScreen(
                    viewModel = authViewModel,
                    onClickNav = { route ->
                        navController.navigate(route)
                    },
                    onBack = { navController.navigateUp() },
                )
            }
            composable(
                route = Routes.Details.routes,
                arguments = DetailNavTypeDestination.argumentList,
            ) { backStackEntry ->
                val (movie) = DetailNavTypeDestination.parseArguments(backStackEntry)
                MovieDetailScreen(
                    movieId = movie.id,
                    onBack = { navController.navigateUp() },
                )
            }
        }

        // Banner visibility: Only show for authenticated users
        // This automatically excludes: Login, Register, and any pre-auth screens
        if (authState is AuthState.Authenticated) {
            AdaptiveBanner()
        }
    }
}
