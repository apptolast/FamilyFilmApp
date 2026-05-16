import GoogleMobileAds
import UIKit
import ComposeApp

final class IOSBannerAdViewFactory: NSObject, BannerAdViewFactory {

    func createBannerView(adUnitId: String, width: Double) -> UIView {
        let adSize = currentOrientationAnchoredAdaptiveBanner(width: CGFloat(width))
        let banner = BannerView(adSize: adSize)
        banner.adUnitID = adUnitId
        banner.rootViewController = Self.rootViewController()
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
}
