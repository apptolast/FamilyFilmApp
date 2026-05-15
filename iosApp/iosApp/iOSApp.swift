import SwiftUI
import ComposeApp
import AppTrackingTransparency
import FirebaseCore
import FirebaseAppCheck
import GoogleMobileAds
import GoogleSignIn
import RevenueCat
import UserMessagingPlatform

@main
struct iOSApp: App {
    init() {
        // App Check must be configured before FirebaseApp.configure() so the
        // first Firestore/Auth/Functions calls carry a valid attestation token.
        #if DEBUG
        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        #else
        AppCheck.setAppCheckProviderFactory(AppAttestProviderFactory())
        #endif

        FirebaseApp.configure()

        // RevenueCat — App Store SDK key lives in Info.plist (key
        // `RevenueCatAppStoreKey`). If empty we skip configuration to avoid
        // a runtime crash; purchase flows then fail gracefully.
        if let revenueCatKey = Bundle.main.object(forInfoDictionaryKey: "RevenueCatAppStoreKey") as? String,
           !revenueCatKey.isEmpty {
            Purchases.logLevel = .warn
            Purchases.configure(withAPIKey: revenueCatKey)
        }

        // AdMob — kick ATT in parallel so the prompt appears before the
        // first ad request; the `start` call itself is non-blocking.
        ATTrackingManager.requestTrackingAuthorization { _ in
            // Result is observed by AdMob internally.
        }
        MobileAds.shared.start(completionHandler: nil)

        // GoogleSignIn reads `GIDClientID` from Info.plist on first use, so
        // no imperative configure is needed here.

        // Start Koin after Firebase is configured so any singleton resolved
        // on first injection can safely touch Firestore/Auth.
        KoinInitKt.doInitKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // GoogleSignIn callback URL scheme handler.
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}
