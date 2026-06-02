import SwiftUI
import ComposeApp
import AppTrackingTransparency
import FirebaseCore
import FirebaseAppCheck
import FirebaseAnalytics
import FirebaseCrashlytics
import FirebaseMessaging
import GoogleMobileAds
import GoogleSignIn
import RevenueCat
import UserMessagingPlatform
import UserNotifications

final class FamilyFilmAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
    func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
        if #available(iOS 14.0, *) {
            return AppAttestProvider(app: app)
        } else {
            return DeviceCheckProvider(app: app)
        }
    }
}

enum AppDiagnostics {
    static func log(_ message: String) {
        NSLog("[FliksyDiagnostics] \(message)")
        Crashlytics.crashlytics().log(message)
    }

    static func set(_ value: Any, forKey key: String) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }

    static func record(_ message: String, domain: String = "FliksyDiagnostics", code: Int = 0) {
        log("\(domain): \(message)")
        Crashlytics.crashlytics().record(
            error: NSError(
                domain: domain,
                code: code,
                userInfo: [NSLocalizedDescriptionKey: message]
            )
        )
    }

    static func record(_ error: Error, context: String) {
        let nsError = error as NSError
        log("\(context): \(nsError.domain) \(nsError.code) \(nsError.localizedDescription)")
        Crashlytics.crashlytics().record(error: nsError)
    }
}

final class FamilyFilmAppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
        AppDiagnostics.log("APNs device token registered")
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        AppDiagnostics.record(error, context: "APNs registration failed")
    }
}

final class PushNotificationCoordinator: NSObject, UNUserNotificationCenterDelegate, MessagingDelegate {
    static let shared = PushNotificationCoordinator()

    private override init() {
    }

    func configure(application: UIApplication = .shared) {
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self

        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if let error {
                AppDiagnostics.record(error, context: "Notification authorization failed")
            }
            AppDiagnostics.set(granted, forKey: "notifications_authorized")
            AppDiagnostics.log("Notification authorization completed")

            DispatchQueue.main.async {
                application.registerForRemoteNotifications()
            }
        }
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken, !fcmToken.isEmpty else {
            AppDiagnostics.set(false, forKey: "fcm_token_present")
            AppDiagnostics.log("FCM registration token unavailable")
            return
        }

        UserDefaults.standard.set(fcmToken, forKey: "fcm_registration_token")
        AppDiagnostics.set(true, forKey: "fcm_token_present")
        AppDiagnostics.log("FCM registration token refreshed")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        AppDiagnostics.log("Push notification opened")
        completionHandler()
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(FamilyFilmAppDelegate.self) private var appDelegate

    init() {
        // App Check must be configured before FirebaseApp.configure() so the
        // first Firestore/Auth/Functions calls carry a valid attestation token.
        #if DEBUG
        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        #else
        AppCheck.setAppCheckProviderFactory(FamilyFilmAppCheckProviderFactory())
        #endif

        FirebaseApp.configure()
        Analytics.setAnalyticsCollectionEnabled(true)
        PushNotificationCoordinator.shared.configure()
        AppDiagnostics.log("iOS app launched")

        // Install the GIDSignIn bridge so IosGoogleSignInClient can drive the
        // native flow when the user taps "Iniciar sesión con Google".
        MainViewControllerKt.setGoogleSignInBridge(bridge: GoogleSignInBridgeImpl())

        // Install the AuthenticationServices bridge for Sign in with Apple.
        MainViewControllerKt.setAppleSignInBridge(bridge: AppleSignInBridgeImpl())

        // RevenueCat — prefer BuildKonfig so local.properties / CI secrets are
        // the source of truth, keeping Info.plist as a backwards-compatible fallback.
        let revenueCatKey = !BuildConfig.shared.REVENUECAT_APPSTORE_SDK_KEY.isEmpty
            ? BuildConfig.shared.REVENUECAT_APPSTORE_SDK_KEY
            : (
            Bundle.main.object(forInfoDictionaryKey: "RevenueCatAppStoreKey") as? String
        ).flatMap {
            $0.isEmpty ? nil : $0
        } ?? ""
        if !revenueCatKey.isEmpty {
            Purchases.logLevel = .warn
            AppDiagnostics.set(true, forKey: "revenuecat_key_present")
            AppDiagnostics.log("Configuring RevenueCat for App Store")
            Purchases.configure(withAPIKey: revenueCatKey)
            MainViewControllerKt.setRevenueCatPurchaseBridge(bridge: RevenueCatPurchaseBridgeImpl())
        } else {
            NSLog("RevenueCat skipped: RevenueCatAppStoreKey is empty")
            AppDiagnostics.set(false, forKey: "revenuecat_key_present")
            AppDiagnostics.record("RevenueCat skipped: App Store SDK key is empty", domain: "RevenueCat")
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
            AppDiagnostics.log("AdMob MobileAds.start completed")
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
