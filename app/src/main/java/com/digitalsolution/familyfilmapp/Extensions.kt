package com.digitalsolution.familyfilmapp

import androidx.navigation.NavController

fun NavController.popUpToNavigate(firstRoute: String, secondRoute: String) {
    this.navigate(firstRoute) {
        popUpTo(secondRoute) {
            inclusive = true
        }
    }
}