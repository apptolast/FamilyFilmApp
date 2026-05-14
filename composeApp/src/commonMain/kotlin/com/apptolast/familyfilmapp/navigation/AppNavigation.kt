package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

/**
 * Root navigation graph. Block 12 ships this as a scaffold: every destination
 * routes to a placeholder so the type-safe routes are exercised. Block 13
 * fills each `composable<Route> { ... }` body with the real screen, and
 * block 12b adds the Scaffold/TopAppBar/BottomNavigationBar/AdaptiveBanner
 * decoration controlled by the current backstack entry.
 *
 * The start destination flips Login ↔ Home based on the auth state pulled
 * from `AuthViewModel` in block 12b.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Login,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<Routes.Login> { Placeholder("Login") }
        composable<Routes.Home> { Placeholder("Home") }
        composable<Routes.Discover> { Placeholder("Discover") }
        composable<Routes.Chat> { Placeholder("Chat") }
        composable<Routes.Groups> { Placeholder("Groups") }
        composable<Routes.Profile> { Placeholder("Profile") }
        composable<Routes.Details> { entry ->
            val details: Routes.Details = entry.toRoute()
            Placeholder("Details(${details.mediaId}, ${details.mediaType})")
        }
    }
}

@Composable
private fun Placeholder(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$name — coming in block 13")
    }
}
