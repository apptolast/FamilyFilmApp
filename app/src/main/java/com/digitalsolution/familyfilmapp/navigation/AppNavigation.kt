package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalsolution.familyfilmapp.ui.components.BottomBar
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.screens.DetailsScreen
import com.digitalsolution.familyfilmapp.ui.screens.groups.GroupsScreen
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeScreen
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreen
import com.digitalsolution.familyfilmapp.ui.screens.profile.ProfileScreen
import com.digitalsolution.familyfilmapp.ui.screens.recommend.RecommendScreen
import com.digitalsolution.familyfilmapp.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {

    val navController = rememberNavController()

    val navigationUIState by viewModel.navigationUIState.observeAsState()

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = navigationUIState!!.isBottomBarVisible.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                navigationUIState?.titleScreens?.let {
                    TopBar(
                        onClickLogOut = {
                            viewModel.logOut()
                        },
                        title = it.value,
                        groups = viewModel.getGroupsList()
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = navigationUIState!!.isBottomBarVisible.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomBar(navController = navController)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = navigationUIState!!.isBottomBarVisible.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.Search.routes) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = Routes.Login.routes
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
                ProfileScreen(navController = navController)
            }
            composable(route = Routes.Details.routes) {
                DetailsScreen(navController = navController)
            }
            composable(route = Routes.Search.routes) {
                SearchScreen(navController = navController)
            }
        }
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        val isBottomBarVisible = when (destination.route) {
            Routes.Home.routes,
            Routes.Recommend.routes,
            Routes.Groups.routes,
            Routes.Profile.routes -> {
                true
            }

            else -> {
                false
            }
        }
        val titleScreens = when (destination.route) {
            Routes.Home.routes -> "Home"
            Routes.Recommend.routes -> "Recommendations"
            Routes.Groups.routes -> "Groups"
            Routes.Profile.routes -> "Profile"
            Routes.Search.routes -> "Search"
            else -> "Unknown"
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

@Preview
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}
