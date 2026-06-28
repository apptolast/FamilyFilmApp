package com.apptolast.familyfilmapp.screenshot

import com.apptolast.familyfilmapp.model.local.User

/**
 * Opt-in mode for generating App Store screenshots offline. It is activated ONLY by the
 * iOS UI test's launch argument (`-FFADemoMode YES`) and is always OFF in production and
 * on Android. When on, [com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel]
 * skips the Firebase login gate and emits a demo session, and the iOS host skips the
 * ATT / notification / consent / AdMob bootstrap so no system dialog or ad pollutes a shot.
 * Combined with the fake TMDB datasource, the app reaches Home/Discover with fictional
 * titles and original generated posters (Apple Guideline 5.2.1).
 */
object ScreenshotMode {
    var enabled: Boolean = false
        private set

    fun activate() {
        enabled = true
    }
}

/** Hardcoded, offline session used only while [ScreenshotMode.enabled]. */
object ScreenshotDemoData {
    val user = User(
        id = "screenshot-demo-user",
        email = "demo@fliksy.app",
        language = "en",
        photoUrl = "",
        username = "Fliksy",
        hasRemovedAds = true,
    )
}
