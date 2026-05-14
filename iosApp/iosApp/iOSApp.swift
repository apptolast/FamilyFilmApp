import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseAppCheck

// Once the corresponding SPM packages are added in Xcode (see README →
// "Swift Package Manager (iOS only)") the imports below will resolve:
//   import GoogleMobileAds
//   import RevenueCat
//   import GoogleSignIn
//   import UserMessagingPlatform
// Add them back when each module is wired.

@main
struct iOSApp: App {
    init() {
        // Configure App Check BEFORE FirebaseApp.configure() so the first
        // Firestore/Auth/Functions calls go out with a valid attestation
        // token.
        #if DEBUG
        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        #endif

        FirebaseApp.configure()

        // Block 15 follow-up — uncomment once the SPM packages are wired:
        //
        // GoogleMobileAds:
        //   MobileAds.shared.start { _ in }
        //
        // ATT (App Tracking Transparency) — required before AdMob personalised ads:
        //   ATTrackingManager.requestTrackingAuthorization { _ in }
        //
        // RevenueCat:
        //   Purchases.logLevel = .warn
        //   Purchases.configure(withAPIKey: "<paste BuildConfig.REVENUECAT_APPSTORE_SDK_KEY here>")
        //
        // GoogleSignIn — set the GIDClientID from BuildConfig.WEB_ID_CLIENT
        // in Info.plist (Info.plist key `GIDClientID`). The URL scheme
        // handler is registered through application(_:open:options:).

        // Start Koin after Firebase is configured so any singleton resolved
        // on first injection can safely touch Firestore/Auth.
        KoinInitKt.initKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
