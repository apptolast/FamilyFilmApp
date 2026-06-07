package com.apptolast.familyfilmapp.ads

import com.apptolast.familyfilmapp.BuildConfig

object AndroidAdUnitIds {
    fun banner(): String = BuildConfig.ADMOB_BOTTOM_BANNER_ID

    fun nativeHome(): String = BuildConfig.ADMOB_NATIVE_HOME_ID

    fun appOpen(): String = BuildConfig.ADMOB_APP_OPEN_ID

    fun source(): String = "buildkonfig"

    fun suffixSafe(adUnitId: String): String = if (adUnitId.isBlank()) "<empty>" else adUnitId.takeLast(8)
}
