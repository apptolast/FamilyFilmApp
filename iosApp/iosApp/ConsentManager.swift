import Foundation
import UIKit
import GoogleMobileAds
import UserMessagingPlatform

/// Gathers Google UMP consent and starts AdMob. App Tracking Transparency is requested
/// earlier and independently (see `AppBootstrap`) so the ATT prompt can never collide with
/// another system alert nor be dropped because the app is not foreground-active — the
/// failure mode that kept ATT from appearing on iOS/iPadOS 26.
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
                if ConsentInformation.shared.canRequestAds {
                    self.startMobileAdsOnce(onReady: onReady)
                }
            }
        }

        // Fast-path: a previous session already granted consent.
        if ConsentInformation.shared.canRequestAds {
            startMobileAdsOnce(onReady: onReady)
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
