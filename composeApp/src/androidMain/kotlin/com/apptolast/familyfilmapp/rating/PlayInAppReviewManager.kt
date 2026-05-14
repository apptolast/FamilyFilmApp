package com.apptolast.familyfilmapp.rating

import android.content.Context
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android implementation backed by Play In-App Review. The Play Store
 * deduplicates review prompts behind the scenes and may decide not to
 * show one — there's no way to know whether the user actually rated.
 * `hasRatedApp` only reflects that the user tapped the "rate" entry
 * point at least once (we cache that locally via [MutableStateFlow] —
 * future work could persist it via `multiplatform-settings`).
 */
class PlayInAppReviewManager(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
    private val crashReporter: CrashReporter,
) : RateAppManager {

    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context)

    private val _hasRatedApp = MutableStateFlow(false)
    override val hasRatedApp: StateFlow<Boolean> = _hasRatedApp.asStateFlow()

    override fun markAsRated() {
        val activity = activityHolder.current ?: run {
            _hasRatedApp.value = true
            return
        }
        reviewManager.requestReviewFlow()
            .addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    reviewManager.launchReviewFlow(activity, request.result)
                        .addOnCompleteListener { _hasRatedApp.value = true }
                } else {
                    crashReporter.recordException(
                        request.exception ?: Exception("Play In-App Review request failed"),
                    )
                    _hasRatedApp.value = true
                }
            }
    }
}
