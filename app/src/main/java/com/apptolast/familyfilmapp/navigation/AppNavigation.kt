package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.AdaptiveBanner
import com.apptolast.familyfilmapp.ui.components.BottomNavigationBar
import com.apptolast.familyfilmapp.ui.screens.detail.MovieDetailScreen
import com.apptolast.familyfilmapp.ui.screens.discover.DiscoverScreen
import com.apptolast.familyfilmapp.ui.screens.groups.GroupsScreen
import com.apptolast.familyfilmapp.ui.screens.home.HomeScreen
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileScreen
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel

/**
 * Main navigation component for the app.
 * Implements unified TopBar, bottom navigation bar and a persistent banner across authenticated screens.
 *
 * TopBar visibility logic:
 * - Login/Register/Details screens: NO top bar
 * - Home, Discover, Groups, Profile: YES top bar with screen title
 *
 * BottomNavigationBar visibility logic:
 * - Login/Register screens: NO bottom bar (user not authenticated)
 * - Home, Discover, Groups, Profile: YES bottom bar (main authenticated screens)
 * - Details: NO bottom bar (secondary screen)
 *
 * Banner visibility logic:
 * - Login/Register screens: NO banner (user not authenticated)
 * - SplashScreen: NO banner (handled by authState check)
 * - Home, Discover, Groups, Profile, Details: YES banner (user authenticated)
 *
 * The banner is positioned at the app level (outside NavHost) to prevent
 * recreation on navigation, ensuring smooth UX and efficient ad loading.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine which screens should show TopBar and BottomBar
    val showTopBar = authState is AuthState.Authenticated &&
        currentRoute in listOf(
            Routes.Home.routes,
            Routes.Discover.routes,
            Routes.Groups.routes,
            Routes.Profile.routes,
        )

    val showBottomBar = showTopBar // Same screens show both

    // Get the title for the current route
    val titleRes = when (currentRoute) {
        Routes.Home.routes -> Routes.Home.title
        Routes.Discover.routes -> Routes.Discover.title
        Routes.Groups.routes -> Routes.Groups.title
        Routes.Profile.routes -> Routes.Profile.title
        else -> null
    }

    Scaffold(
        topBar = {
            if (showTopBar && titleRes != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                Column {
                    AdaptiveBanner()
                    BottomNavigationBar(
                        navController = navController,
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (authState is AuthState.Authenticated) {
                Routes.Home.routes
            } else {
                Routes.Login.routes
            },
            modifier = Modifier.padding(paddingValues),
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
            composable(route = Routes.Discover.routes) {
                DiscoverScreen(
                    onMovieClick = { movieId ->
                        // TODO: Navigate to details with movie ID
                        // For now, we need to fetch the movie first
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
    }
}
