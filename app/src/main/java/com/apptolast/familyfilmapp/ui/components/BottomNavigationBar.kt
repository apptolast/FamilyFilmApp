package com.apptolast.familyfilmapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.apptolast.familyfilmapp.navigation.Routes

/**
 * Bottom Navigation Bar for main app screens.
 * Displays 4 tabs: Home, Discover, Groups, Profile
 *
 * Only visible on main authenticated screens (not on Details or Login)
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<Routes> = listOf(
        Routes.Home,
        Routes.Discover,
        Routes.Groups,
        Routes.Profile,
    ),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(screen.title),
                        )
                    }
                },
                label = { Text(stringResource(screen.title)) },
                selected = currentRoute == screen.routes,
                onClick = {
                    if (currentRoute != screen.routes) {
                        navController.navigate(screen.routes) {
                            // Pop up to start destination to avoid building a large stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when navigating back
                            restoreState = true
                        }
                    }
                },
            )
        }
    }
}
