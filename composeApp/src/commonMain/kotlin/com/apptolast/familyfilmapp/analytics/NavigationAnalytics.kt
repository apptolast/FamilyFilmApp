package com.apptolast.familyfilmapp.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

// Firebase's built-in screen tracking only fires on Activity changes, so log manually here.
@Composable
fun TrackScreenViews(navController: NavController, tracker: AnalyticsTracker) {
    LaunchedEffect(navController, tracker) {
        navController.currentBackStackEntryFlow
            .mapNotNull { it.destination.route }
            .distinctUntilChanged()
            .collect { route ->
                tracker.logScreenView(screenName = route.substringAfterLast('.'))
            }
    }
}
