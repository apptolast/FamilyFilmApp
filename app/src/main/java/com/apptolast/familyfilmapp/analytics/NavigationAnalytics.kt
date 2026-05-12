package com.apptolast.familyfilmapp.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.navigation.Routes
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

/**
 * Observes the current navigation destination and logs a `screen_view` event each time it
 * changes. Firebase Analytics auto-tracks Activity screen views but NOT Compose destinations,
 * so this manual hook covers our single-Activity Compose app.
 */
@Composable
fun TrackScreenViews(navController: NavController, tracker: AnalyticsTracker) {
    LaunchedEffect(navController, tracker) {
        navController.currentBackStackEntryFlow
            .mapNotNull { it.destination.route }
            .distinctUntilChanged()
            .collect { route ->
                tracker.logScreenView(screenName = route.toScreenName())
            }
    }
}

private fun String.toScreenName(): String = when (this) {
    Routes.Login.routes -> "login"
    Routes.Home.routes -> "home"
    Routes.Discover.routes -> "discover"
    Routes.Chat.routes -> "chat"
    Routes.Groups.routes -> "groups"
    Routes.Profile.routes -> "profile"
    Routes.Details.routes -> "details"
    else -> substringBefore("/").substringBefore("?").ifBlank { "unknown" }
}
