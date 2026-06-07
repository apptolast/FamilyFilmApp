import GoogleMobileAds
import UIKit
import ComposeApp

final class IOSBannerAdViewFactory: NSObject, BannerAdViewFactory {

    private var delegates: [BannerAdDelegate] = []

    func createBannerView(adUnitId: String, width: Double) -> UIView {
        let adSize = currentOrientationAnchoredAdaptiveBanner(width: CGFloat(width))
        let banner = BannerView(adSize: adSize)
        banner.adUnitID = adUnitId
        banner.rootViewController = Self.rootViewController()
        let delegate = BannerAdDelegate(adUnitId: adUnitId)
        delegates.append(delegate)
        banner.delegate = delegate
        if adUnitId.isEmpty {
            AppDiagnostics.record("Banner ad unit id is empty", domain: "AdMob")
        }
        if banner.rootViewController == nil {
            AppDiagnostics.record("Banner rootViewController is nil", domain: "AdMob")
        }
        AppDiagnostics.log("Loading banner ad unit suffix=\(Self.suffix(adUnitId)) width=\(width)")
        banner.load(Request())
        return banner
    }

    func getBannerHeight(width: Double) -> Double {
        Double(currentOrientationAnchoredAdaptiveBanner(width: CGFloat(width)).size.height)
    }

    private static func rootViewController() -> UIViewController? {
        UIApplication.shared.connectedScenes
        .compactMap {
            $0 as? UIWindowScene
        }
        .flatMap {
            $0.windows
        }
        .first {
            $0.isKeyWindow
        }?
        .rootViewController
    }

    private static func suffix(_ value: String) -> String {
        value.isEmpty ? "<empty>" : String(value.suffix(8))
    }

    private final class BannerAdDelegate: NSObject, BannerViewDelegate {
        private let adUnitId: String

        init(adUnitId: String) {
            self.adUnitId = adUnitId
        }

        func bannerViewDidReceiveAd(_ bannerView: BannerView) {
            AppDiagnostics.log("Banner loaded suffix=\(IOSBannerAdViewFactory.suffix(adUnitId))")
        }

        func bannerView(_ bannerView: BannerView, didFailToReceiveAdWithError error: Error) {
            AppDiagnostics.record(error, context: "Banner failed suffix=\(IOSBannerAdViewFactory.suffix(adUnitId))")
        }
    }
}
