import SwiftUI
import UIKit
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

    func configure(application: UIApplication = .shared, onAuthorizationFinished: @escaping () -> Void = {
    }) {
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
                // The notification dialog has now been dismissed, so the next system
                // alert (ATT) can be presented without colliding with it.
                onAuthorizationFinished()
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

        subscribeToReleaseTopic()
    }

    // Subscribe to a single language-scoped "new releases" topic so the n8n workflow
    // can fan out localized push notifications. Self-heals on language change by
    // dropping the other language's topic.
    private func subscribeToReleaseTopic() {
        let lang = Locale.current.language.languageCode?.identifier == "es" ? "es" : "en"
        let other = lang == "es" ? "en" : "es"
        Messaging.messaging().subscribe(toTopic: "new_releases_\(lang)")
        Messaging.messaging().unsubscribe(fromTopic: "new_releases_\(other)")
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

/// Orchestrates the privacy/consent chain once the app is foreground-active.
///
/// App Tracking Transparency MUST be requested while the app is `.active` and on the main
/// thread; otherwise iOS presents nothing and returns `.notDetermined` (the recurring
/// "ATT prompt not found" rejection). We therefore request ATT FIRST — before the
/// notification prompt, the UMP form, AdMob and any tracking — decoupled from any network
/// round-trip, and only when the status is still undetermined. The rest of the chain runs
/// in its completion.
final class AppBootstrap {
    static let shared = AppBootstrap()

    private var hasRun = false

    private init() {
    }

    @MainActor
    func runWhenActive() {
        guard !hasRun else {
            return
        }
        hasRun = true
        // Screenshot/demo mode: skip the ATT, notification, UMP and AdMob bootstrap so no
        // system dialog or ad pollutes App Store screenshots. OFF in production.
        let args = ProcessInfo.processInfo.arguments
        if args.contains("-FFADemoMode") || args.contains("FFA_DEMO_MODE") {
            return
        }
        requestTrackingThenContinue()
    }

    @MainActor
    private func requestTrackingThenContinue() {
        guard #available(iOS 14, *) else {
            continueAfterTracking()
            return
        }

        let status = ATTrackingManager.trackingAuthorizationStatus
        guard status == .notDetermined else {
            AppDiagnostics.set(status.rawValue, forKey: "att_status")
            applyTrackingConsent(authorized: status == .authorized)
            continueAfterTracking()
            return
        }

        // Defer one run loop so the window is fully active before the prompt is presented.
        DispatchQueue.main.async {
            ATTrackingManager.requestTrackingAuthorization { newStatus in
                AppDiagnostics.set(newStatus.rawValue, forKey: "att_status")
                AppDiagnostics.log("ATT authorization resolved status=\(newStatus.rawValue)")
                DispatchQueue.main.async {
                    self.applyTrackingConsent(authorized: newStatus == .authorized)
                    self.continueAfterTracking()
                }
            }
        }
    }

    private func applyTrackingConsent(authorized: Bool) {
        // Both FirebaseAnalytics and UserMessagingPlatform export a `ConsentStatus`; qualify it.
        let adConsent: FirebaseAnalytics.ConsentStatus = authorized ? .granted : .denied
        Analytics.setConsent([
            .analyticsStorage: .granted,
            .adStorage: adConsent,
            .adUserData: adConsent,
            .adPersonalization: adConsent,
        ])
    }

    @MainActor
    private func continueAfterTracking() {
        // Notification permission is requested next; its dialog can no longer collide with
        // the already-resolved ATT prompt. Then UMP consent, then AdMob.
        PushNotificationCoordinator.shared.configure {
            let rootVC = UIApplication.shared.connectedScenes
                .compactMap {
                    $0 as? UIWindowScene
                }
                .first?.windows.first?.rootViewController
            ConsentManager.shared.gatherConsent(from: rootVC) {
                AppDiagnostics.log("Consent gathered; loading app open ad")
                AppOpenAdManager.shared.loadAd()
            }
        }
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(FamilyFilmAppDelegate.self) private var appDelegate
    @Environment(\.scenePhase) private var scenePhase

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
        // Default consent denies ad-related storage until the user resolves the ATT
        // prompt. Updated in AppBootstrap once tracking authorization is known.
        Analytics.setConsent([
            .analyticsStorage: .granted,
            .adStorage: .denied,
            .adUserData: .denied,
            .adPersonalization: .denied,
        ])
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

        // AdMob — register Kotlin↔Swift bridges before MobileAds starts so the first
        // composable that needs a banner finds the factory ready.
        BannerAdBridge.shared.factory = IOSBannerAdViewFactory()
        NativeAdBridge.shared.loader = IOSNativeAdLoader()
        NativeAdBridge.shared.viewFactory = IOSNativeAdViewFactory()

        // The privacy/consent chain (ATT → notifications → UMP → AdMob) is started from
        // AppBootstrap on the first scene activation, NOT here. `init()` runs before the
        // scene is foreground-active, and iOS silently drops the ATT prompt when it is
        // requested while the app is not active — the bug that kept ATT from appearing.

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
                .onChange(of: scenePhase) { phase in
                    if phase == .active {
                        AppBootstrap.shared.runWhenActive()
                    }
                }
                .onAppear {
                    // Fallback in case the initial transition into `.active` is missed.
                    if scenePhase == .active {
                        AppBootstrap.shared.runWhenActive()
                    }
                }
        }
    }
}
