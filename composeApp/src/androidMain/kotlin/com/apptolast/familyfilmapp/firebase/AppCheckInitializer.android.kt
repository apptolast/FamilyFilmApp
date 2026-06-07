package com.apptolast.familyfilmapp.firebase

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

actual fun installAppCheckProvider(debug: Boolean) {
    val factory = if (debug) {
        debugAppCheckProviderFactory()
    } else {
        PlayIntegrityAppCheckProviderFactory.getInstance()
    }
    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)
}

private fun debugAppCheckProviderFactory(): AppCheckProviderFactory {
    val providerClass = Class.forName("com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory")
    val getInstance = providerClass.getMethod("getInstance")
    return getInstance.invoke(null) as AppCheckProviderFactory
}
