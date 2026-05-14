package com.apptolast.familyfilmapp.platform

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * Tracks the currently visible Activity so platform-only services
 * (CredentialManager, RevenueCat Purchases.purchase(...), AdMob banners,
 * Play In-App Review) can launch their UI without taking the Activity as
 * a parameter on every call.
 *
 * MainActivity wires this in `onCreate` (set) and `onDestroy` (clear).
 * Holding a [WeakReference] avoids leaking the Activity if the app is
 * destroyed before a coroutine that captured it completes.
 */
class CurrentActivityHolder {
    private var ref: WeakReference<Activity>? = null

    val current: Activity? get() = ref?.get()

    fun attach(activity: Activity) {
        ref = WeakReference(activity)
    }

    fun detach(activity: Activity) {
        if (ref?.get() === activity) ref = null
    }
}
