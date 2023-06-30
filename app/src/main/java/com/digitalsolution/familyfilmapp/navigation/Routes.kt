package com.digitalsolution.familyfilmapp.navigation

sealed class Routes(val routes: String) {
    object SplashScreenDest : Routes("splash_screen")
    object Login : Routes("login")
}
