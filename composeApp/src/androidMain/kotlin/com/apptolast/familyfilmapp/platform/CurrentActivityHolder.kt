package com.apptolast.familyfilmapp.platform

import android.app.Activity
import java.lang.ref.WeakReference

// WeakReference avoids leaking the Activity across coroutines that outlive it.
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
