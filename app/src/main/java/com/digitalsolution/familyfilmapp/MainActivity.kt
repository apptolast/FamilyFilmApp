package com.digitalsolution.familyfilmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.digitalsolution.familyfilmapp.navigation.AppNavigation
import com.digitalsolution.familyfilmapp.navigation.Routes
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyFilmAppTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme

                val navController: NavHostController = rememberNavController()

                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomBar(navController = navController) }
                ) { paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues)) {
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {

    val screens = listOf(
        Routes.Home,
        Routes.Recommend,
        Routes.Filter,
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = screen.icon, contentDescription = "")
                },
                label = {
                    Text(text = screen.routes)
                },
            )
        }
    }
}
