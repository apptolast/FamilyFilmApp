package com.apptolast.familyfilmapp.purchases

import com.apptolast.familyfilmapp.firebase.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS scaffold for [PurchaseManager]. RevenueCat ships a Swift Package
 * (`https://github.com/RevenueCat/purchases-ios-spm.git` → product
 * `RevenueCat`) which is added in Xcode at integration time (see README
 * → "Swift Package Manager (iOS only)").
 *
 * Wiring once the SPM module is added:
 *
 * 1. Xcode: File → Add Package Dependencies → RevenueCat purchases-ios-spm,
 *    select the `RevenueCat` product on the `iosApp` target.
 *
 * 2. cinterop `.def` at composeApp/src/nativeInterop/cinterop/RevenueCat.def:
 *      language = Objective-C
 *      modules = RevenueCat
 *      package = cocoapods.RevenueCat
 *      linkerOpts = -framework RevenueCat
 *    referenced from composeApp/build.gradle.kts cinterops {} on
 *    iosArm64 + iosSimulatorArm64.
 *
 * 3. In iosApp/iosApp/iOSApp.swift after FirebaseApp.configure() add:
 *      Purchases.logLevel = .warn
 *      Purchases.configure(withAPIKey: ProcessInfo.processInfo
 *         .environment["REVENUECAT_APPSTORE_SDK_KEY"] ?? "")
 *    Or alternatively expose `BuildConfig.REVENUECAT_APPSTORE_SDK_KEY`
 *    to the iOS app through a generated Info.plist value.
 *
 * 4. Replace this scaffold with a class that mirrors
 *    [com.apptolast.familyfilmapp.purchases.RevenueCatPurchaseManager]
 *    (the Android side): `Purchases.shared.logIn(userId, ...)`,
 *    `Purchases.shared.purchase(package:)`,
 *    `Purchases.shared.restorePurchases()` plus a
 *    `PurchasesDelegate` to mirror entitlement changes into the
 *    StateFlows.
 *
 * Until then every paywall call resolves to
 * [PurchaseFailure.Cancelled] so the UI gracefully no-ops.
 */
class IosRevenueCatPurchaseManager(
    private val crashReporter: CrashReporter,
) : PurchaseManager {

    private val _hasRemovedAds = MutableStateFlow(false)
    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()

    private val _hasChatPremium = MutableStateFlow(false)
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    override suspend fun initialize(userId: String) {
        crashReporter.log("IosRevenueCatPurchaseManager.initialize($userId) — SPM cinterop not wired yet")
    }

    override fun setAdsRemoved(value: Boolean) {
        _hasRemovedAds.value = value
    }

    override fun logout() {
        _hasRemovedAds.value = false
        _hasChatPremium.value = false
    }

    override suspend fun purchaseRemoveAds(): Result<Unit> = Result.failure(PurchaseFailure.Cancelled)
    override suspend fun purchaseChatPremium(): Result<Unit> = Result.failure(PurchaseFailure.Cancelled)
    override suspend fun restorePurchases(): Result<Boolean> = Result.success(false)
}
