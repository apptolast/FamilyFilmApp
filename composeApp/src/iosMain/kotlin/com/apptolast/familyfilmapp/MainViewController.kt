package com.apptolast.familyfilmapp

import androidx.compose.ui.window.ComposeUIViewController
import com.apptolast.familyfilmapp.auth.IosAppleSignInBridge
import com.apptolast.familyfilmapp.auth.IosAppleSignInClient
import com.apptolast.familyfilmapp.auth.IosGoogleSignInBridge
import com.apptolast.familyfilmapp.auth.IosGoogleSignInClient
import com.apptolast.familyfilmapp.di.initKoin
import com.apptolast.familyfilmapp.purchases.IosRevenueCatPurchaseBridge
import com.apptolast.familyfilmapp.purchases.IosRevenueCatPurchaseManager
import com.apptolast.familyfilmapp.screenshot.ScreenshotMode
import platform.Foundation.NSProcessInfo

// Module-level flag guards against multiple ComposeUIViewController
// instantiations (e.g. SwiftUI re-rendering the host view). The Koin call
// itself is also idempotent — this is just belt-and-braces.
private var koinInitialized = false

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController {
    if (!koinInitialized) {
        val demoMode = isDemoModeRequested()
        if (demoMode) ScreenshotMode.activate()
        initKoin(demoMode = demoMode)
        koinInitialized = true
    }
    App()
}

// Demo/screenshot mode is opt-in via process launch arguments and is OFF in
// production. The screenshot UI test passes `-FFADemoMode YES`, which iOS
// surfaces as two consecutive arguments; we also accept a bare `FFA_DEMO_MODE`
// flag for convenience. When on, initKoin swaps in the offline fake datasource.
private fun isDemoModeRequested(): Boolean {
    val args = NSProcessInfo.processInfo.arguments.filterIsInstance<String>()
    if (args.any { it == "FFA_DEMO_MODE" }) return true
    val flagIndex = args.indexOf("-FFADemoMode")
    return flagIndex >= 0 && args.getOrNull(flagIndex + 1) == "YES"
}

// Swift-callable entry point: installs the GIDSignIn bridge implemented in iosApp/.
fun setGoogleSignInBridge(bridge: IosGoogleSignInBridge) {
    IosGoogleSignInClient.installBridge(bridge)
}

// Swift-callable entry point: installs the AuthenticationServices bridge implemented in iosApp/.
fun setAppleSignInBridge(bridge: IosAppleSignInBridge) {
    IosAppleSignInClient.installBridge(bridge)
}

// Swift-callable entry point: installs the RevenueCat bridge implemented in iosApp/.
fun setRevenueCatPurchaseBridge(bridge: IosRevenueCatPurchaseBridge) {
    IosRevenueCatPurchaseManager.installBridge(bridge)
}
