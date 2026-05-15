package com.apptolast.familyfilmapp

import androidx.compose.ui.window.ComposeUIViewController
import com.apptolast.familyfilmapp.auth.IosGoogleSignInBridge
import com.apptolast.familyfilmapp.auth.IosGoogleSignInClient
import com.apptolast.familyfilmapp.di.initKoin

// Module-level flag guards against multiple ComposeUIViewController
// instantiations (e.g. SwiftUI re-rendering the host view). The Koin call
// itself is also idempotent — this is just belt-and-braces.
private var koinInitialized = false

fun MainViewController() = ComposeUIViewController {
    if (!koinInitialized) {
        initKoin()
        koinInitialized = true
    }
    App()
}

// Swift-callable entry point: installs the GIDSignIn bridge implemented in iosApp/.
fun setGoogleSignInBridge(bridge: IosGoogleSignInBridge) {
    IosGoogleSignInClient.installBridge(bridge)
}
