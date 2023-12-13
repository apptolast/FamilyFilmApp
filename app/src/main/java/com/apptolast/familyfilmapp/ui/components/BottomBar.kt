package com.apptolast.familyfilmapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.apptolast.familyfilmapp.navigation.Routes

@Composable
fun BottomBar(navController: NavController) {
    val screens = listOf(
        Routes.Home,
        Routes.Groups,
        Routes.Profile,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        screens.forEach { screen ->

            NavigationBarItem(
                selected = currentRoute == screen.routes,
                onClick = {
                    navController.navigate(screen.routes) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(imageVector = screen.icon!!, contentDescription = "")
                },
                label = {
                    Text(text = stringResource(id = screen.title))
                },
                alwaysShowLabel = false,
            )
        }
    }
}
