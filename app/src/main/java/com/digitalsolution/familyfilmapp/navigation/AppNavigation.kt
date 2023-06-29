package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = Routes.SplashScreenDest.routes) {

        composable(route = Routes.SplashScreenDest.routes) {
            SplashScreen(navController = navController)
        }

    }
}