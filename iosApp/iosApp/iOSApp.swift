import SwiftUI
import ComposeApp
import AppTrackingTransparency
import FirebaseCore
import FirebaseAppCheck
import GoogleMobileAds
import GoogleSignIn
import RevenueCat
import UserMessagingPlatform

final class FamilyFilmAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
    func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
        if #available(iOS 14.0, *) {
            return AppAttestProvider(app: app)
        } else {
            return DeviceCheckProvider(app: app)
        }
    }
}

@main
struct iOSApp: App {
    init() {
        // App Check must be configured before FirebaseApp.configure() so the
        // first Firestore/Auth/Functions calls carry a valid attestation token.
        #if DEBUG
        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        #else
        AppCheck.setAppCheckProviderFactory(FamilyFilmAppCheckProviderFactory())
        #endif

        FirebaseApp.configure()

        // Install the GIDSignIn bridge so IosGoogleSignInClient can drive the
        // native flow when the user taps "Iniciar sesión con Google".
        MainViewControllerKt.setGoogleSignInBridge(bridge: GoogleSignInBridgeImpl())

        // Install the AuthenticationServices bridge for Sign in with Apple.
        MainViewControllerKt.setAppleSignInBridge(bridge: AppleSignInBridgeImpl())

        // RevenueCat — App Store SDK key lives in Info.plist (key
        // `RevenueCatAppStoreKey`). If empty we skip configuration to avoid
        // a runtime crash; purchase flows then fail gracefully.
        let revenueCatKey = (
            Bundle.main.object(forInfoDictionaryKey: "RevenueCatAppStoreKey") as? String
        ).flatMap {
            $0.isEmpty ? nil : $0
        } ?? BuildConfig.shared.REVENUECAT_APPSTORE_SDK_KEY
        if !revenueCatKey.isEmpty {
            Purchases.logLevel = .warn
            Purchases.configure(withAPIKey: revenueCatKey)
            MainViewControllerKt.setRevenueCatPurchaseBridge(bridge: RevenueCatPurchaseBridgeImpl())
        } else {
            NSLog("RevenueCat skipped: RevenueCatAppStoreKey is empty")
        }

        // AdMob — register Kotlin↔Swift bridges before MobileAds.start so the first
        // composable that needs a banner finds the factory ready.
        BannerAdBridge.shared.factory = IOSBannerAdViewFactory()
        NativeAdBridge.shared.loader = IOSNativeAdLoader()
        NativeAdBridge.shared.viewFactory = IOSNativeAdViewFactory()

        ATTrackingManager.requestTrackingAuthorization { _ in
            // Result is observed by AdMob internally.
        }
        MobileAds.shared.start { _ in
            AppOpenAdManager.shared.loadAd()
        }

        // GoogleSignIn reads `GIDClientID` from Info.plist on first use, so
        // no imperative configure is needed here.
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
