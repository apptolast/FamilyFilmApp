package com.apptolast.familyfilmapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.apptolast.familyfilmapp.navigation.Routes
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.screen_title_chat
import familyfilmkmp.composeapp.generated.resources.screen_title_discover
import familyfilmkmp.composeapp.generated.resources.screen_title_groups
import familyfilmkmp.composeapp.generated.resources.screen_title_home
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

// UI metadata kept here (not on Routes) so the nav layer stays free of Compose imports.
private data class BottomTab(
    val routeClass: KClass<out Routes>,
    val route: Routes,
    val title: StringResource,
    val icon: ImageVector,
)

private val tabs: List<BottomTab> = listOf(
    BottomTab(Routes.Home::class, Routes.Home, Res.string.screen_title_home, Icons.Outlined.Home),
    BottomTab(Routes.Discover::class, Routes.Discover, Res.string.screen_title_discover, Icons.Outlined.Explore),
    BottomTab(Routes.Chat::class, Routes.Chat, Res.string.screen_title_chat, Icons.AutoMirrored.Outlined.Chat),
    BottomTab(Routes.Groups::class, Routes.Groups, Res.string.screen_title_groups, Icons.Outlined.Groups),
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val backStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        tabs.forEach { tab ->
            val selected = backStackEntry?.destination?.hasRoute(tab.routeClass) == true
            NavigationBarItem(
                icon = {
                    Icon(imageVector = tab.icon, contentDescription = stringResource(tab.title))
                },
                label = if (selected) {
                    { Text(stringResource(tab.title)) }
                } else {
                    null
                },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        }
    }
}
