package com.apptolast.familyfilmapp.rating

import android.content.Context
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Play Store may decide not to show the prompt; hasRatedApp only reflects that the user tapped "rate".
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
