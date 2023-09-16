package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.digitalsolution.familyfilmapp.ui.screens.filter.FilterScreen
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeScreen
import com.digitalsolution.familyfilmapp.ui.screens.profile.ProfileScreen
import com.digitalsolution.familyfilmapp.ui.screens.recommend.RecommendScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.Home.routes
    ) {
        composable(route = Routes.Home.routes) {
            HomeScreen(navController = navController)
        }
        composable(route = Routes.Recommend.routes) {
            RecommendScreen()
        }
        composable(route = Routes.Filter.routes) {
            FilterScreen()
        }
        composable(route = Routes.Profile.routes) {
            ProfileScreen()
        }
    }
}

@Preview
@Composable
fun AppNavigationPreview() {
    AppNavigation(navController = NavHostController(LocalContext.current))
}
