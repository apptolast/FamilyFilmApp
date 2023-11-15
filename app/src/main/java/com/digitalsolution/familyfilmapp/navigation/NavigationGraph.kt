package com.digitalsolution.familyfilmapp.navigation

import androidx.navigation.NavHostController

class NavigationGraph(private val navHostController: NavHostController) {
    val openDetailPage: (String, String, String, Float, Boolean, String) -> Unit =
        { image, title, date, voteAverage, isAdult, synopsis ->
            navHostController.navigate(
                DetailPageDestination.getDestination(image, title, date, voteAverage, isAdult, synopsis),
            )
        }
}
