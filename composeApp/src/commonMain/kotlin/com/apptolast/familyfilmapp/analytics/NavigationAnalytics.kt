package com.apptolast.familyfilmapp.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

/**
 * Observes the current navigation destination and logs a `screen_view`
 * event on every change. Firebase Analytics' built-in screen tracking
 * only fires on Activity changes, so this hook covers our single-Activity
 * Compose app (Android) and the Compose-driven iOS app.
 *
 * The destination route comes from `NavDestination.route` which is the
 * fully qualified `@Serializable` route class name (Navigation Compose
 * MP 2.9). For nicer analytics labels block 14 can map known routes to
 * friendly strings; for now we keep the raw route which is still useful
 * for distinguishing screens in the dashboard.
 */
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
