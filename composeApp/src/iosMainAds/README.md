# iosMainAds — cinterop-based iOS ads (currently inactive)

This source dir is NOT compiled by default. It contains the Kotlin/Native
implementations that use the GoogleMobileAds cinterop bindings declared at
`composeApp/src/nativeInterop/cinterop/GoogleMobileAds.def`.

The current iOS app uses the Swift side (`iosApp/iosApp/iOSApp.swift`) to
initialise the GoogleMobileAds, RevenueCat and GoogleSignIn SDKs at process
start. Kotlin-side iOS uses no-op stubs under `src/iosMain/.../ui/components/`
and `src/iosMain/.../ads/IosAdsFactory.kt`.

To activate the Kotlin-side cinterop path later:

1. In `composeApp/build.gradle.kts`, uncomment the `iosTarget.compilations
   .getByName("main").cinterops { ... }` block.
2. Re-add a `val iosAdsSourceDir = ...` conditional based on
   `localProperty("xcode.frameworks.path")` and wire it via
   `iosMain { kotlin.srcDir(iosAdsSourceDir) }`.
3. Move `src/iosMain/.../ui/components/{AdaptiveBanner,NativeAdSlot}.ios.kt`
   and `src/iosMain/.../ads/IosAdsFactory.kt` to a new
   `src/iosMainNoAds/kotlin/...` mirror dir.
4. Set `xcode.frameworks.path` in `local.properties` to the SPM-resolved
   `PackageFrameworks` directory in Xcode's DerivedData.
