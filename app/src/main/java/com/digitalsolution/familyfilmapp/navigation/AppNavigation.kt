package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalsolution.familyfilmapp.R
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
    viewModel: NavigationViewModel = hiltViewModel(),
    onAddGroup: () -> Unit,
) {
    val navController = rememberNavController()

    val navigationUIState by viewModel.navigationUIState.observeAsState()

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = navigationUIState!!.isBottomBarVisible.value,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                TopBar()
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = navigationUIState!!.isBottomBarVisible.value,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                BottomBar(navController = navController)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = navigationUIState!!.isBottomBarVisible.value,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                when (navigationUIState!!.titleScreens.value) {
                    R.string.screen_title_home -> {
                        FloatingActionButton(
                            onClick = { navController.navigate(Routes.Search.routes) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    }

                    R.string.screen_title_search -> {
                        FloatingActionButton(
                            onClick = { navController.navigate(Routes.Search.routes) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    }

                    R.string.screen_title_groups -> {
                        ExtendedFloatingActionButton(
                            text = { Text(text = stringResource(id = R.string.groups_text_add)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "",
                                )
                            },
                            onClick = { onAddGroup() },
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
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

@Preview
@Composable
fun AppNavigationPreview() {
    AppNavigation {
    }
}
