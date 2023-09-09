package com.digitalsolution.familyfilmapp.navigation

sealed class Routes(val routes: String) {
    object SplashScreen : Routes("splash_screen")
    object Login : Routes("login")
    object Home : Routes("home")
}
