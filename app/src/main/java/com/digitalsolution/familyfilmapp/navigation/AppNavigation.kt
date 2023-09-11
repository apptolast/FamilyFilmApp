package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalsolution.familyfilmapp.ui.screens.home.screen.HomeScreen
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreen
import com.digitalsolution.familyfilmapp.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.routes
    ) {
        composable(route = Routes.SplashScreen.routes) {
            SplashScreen(navController = navController)
        }

        composable(route = Routes.Login.routes) {
            LoginScreen(navController = navController)
        }

        composable(route = Routes.Home.routes) {
            HomeScreen(navController = navController)
        }
    }
}

@Preview
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}
