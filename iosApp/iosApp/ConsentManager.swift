import Foundation
import UIKit
import AppTrackingTransparency
import GoogleMobileAds
import UserMessagingPlatform

/// Gathers Google UMP consent and requests App Tracking Transparency before starting
/// AdMob. ATT is requested from inside the UMP `requestConsentInfoUpdate` completion,
/// which runs on the main thread after a network round-trip — by which point the app is
/// foreground-active, the only state in which iOS presents the ATT prompt. This mirrors
/// the proven flow used in the InemSellar app.
final class ConsentManager {
    static let shared = ConsentManager()

    private var mobileAdsStarted = false

    private init() {
    }

    func gatherConsent(from rootViewController: UIViewController?, onReady: @escaping () -> Void) {
        let parameters = RequestParameters()

        ConsentInformation.shared.requestConsentInfoUpdate(with: parameters) { [weak self] requestError in
            guard let self = self else {
                return
            }
            if let requestError = requestError {
                AppDiagnostics.record(requestError, context: "UMP requestConsentInfoUpdate")
            }

            Task { @MainActor in
                if let vc = rootViewController {
                    do {
                        try await ConsentForm.loadAndPresentIfRequired(from: vc)
                    } catch {
                        AppDiagnostics.record(error, context: "UMP loadAndPresentIfRequired")
                    }
                }
                self.requestATTIfNeeded()
                if ConsentInformation.shared.canRequestAds {
                    self.startMobileAdsOnce(onReady: onReady)
                }
            }
        }

        // Fast-path: a previous session already granted consent.
        if ConsentInformation.shared.canRequestAds {
            requestATTIfNeeded()
            startMobileAdsOnce(onReady: onReady)
        }
    }

    private func requestATTIfNeeded() {
        guard #available(iOS 14, *) else {
            return
        }
        ATTrackingManager.requestTrackingAuthorization { status in
            AppDiagnostics.set(status.rawValue, forKey: "att_status")
            AppDiagnostics.log("ATT authorization resolved status=\(status.rawValue)")
        }
    }

    private func startMobileAdsOnce(onReady: @escaping () -> Void) {
        guard !mobileAdsStarted else {
            return
        }
        mobileAdsStarted = true
        MobileAds.shared.start { _ in
            AppDiagnostics.log("AdMob MobileAds.start completed")
            onReady()
        }
    }
}
