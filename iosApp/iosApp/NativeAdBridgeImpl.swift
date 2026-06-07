import GoogleMobileAds
import UIKit
import ComposeApp

final class IOSNativeAdLoader: NSObject, NativeAdLoader {

    private var loaders: [AdLoader] = []
    private var delegates: [NativeAdDelegate] = []

    func load(adUnitId: String, count: Int32, onLoaded: @escaping (Any) -> Void) {
        if adUnitId.isEmpty {
            AppDiagnostics.record("Native ad unit id is empty", domain: "AdMob")
            return
        }
        let root = rootViewController()
        if root == nil {
            AppDiagnostics.record("Native ad rootViewController is nil", domain: "AdMob")
        }
        AppDiagnostics.log("Loading native ads suffix=\(Self.suffix(adUnitId)) count=\(count)")

        let options = MultipleAdsAdLoaderOptions()
        options.numberOfAds = Int(count)

        let delegate = NativeAdDelegate(
            adUnitId: adUnitId,
            onLoaded: { ad in onLoaded(ad) }
        )
        delegates.append(delegate)

        let loader = AdLoader(
            adUnitID: adUnitId,
            rootViewController: root,
            adTypes: [.native],
            options: [options]
        )
        loader.delegate = delegate
        loader.load(Request())
        loaders.append(loader)
    }

    func destroy(handles: [Any]) {
        // GADNativeAd auto-releases its ad asset when deallocated; we just drop our strong
        // references. Loaders/delegates are owned by this instance and follow its lifetime.
        loaders.removeAll()
        delegates.removeAll()
    }

    private static func suffix(_ value: String) -> String {
        value.isEmpty ? "<empty>" : String(value.suffix(8))
    }

    private func rootViewController() -> UIViewController? {
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

    private final class NativeAdDelegate: NSObject, NativeAdLoaderDelegate {
        private let adUnitId: String
        private let onLoaded: (NativeAd) -> Void

        init(adUnitId: String, onLoaded: @escaping (NativeAd) -> Void) {
            self.adUnitId = adUnitId
            self.onLoaded = onLoaded
        }

        func adLoader(_ adLoader: AdLoader, didReceive nativeAd: NativeAd) {
            AppDiagnostics.log("Native ad loaded suffix=\(IOSNativeAdLoader.suffix(adUnitId))")
            onLoaded(nativeAd)
        }

        func adLoader(_ adLoader: AdLoader, didFailToReceiveAdWithError error: Error) {
            AppDiagnostics.record(error, context: "Native ad failed suffix=\(IOSNativeAdLoader.suffix(adUnitId))")
        }
    }
}

final class IOSNativeAdViewFactory: NSObject, NativeAdViewFactory {

    func createNativeAdView(handle: Any) -> UIView {
        guard let nativeAd = handle as? NativeAd else {
            AppDiagnostics.record("Native ad view received an invalid handle", domain: "AdMob")
            return UIView()
        }

        let adView = NativeAdView()
        adView.backgroundColor = Self.surface
        adView.layer.cornerRadius = 8
        adView.clipsToBounds = true

        let mediaView = MediaView()
        mediaView.translatesAutoresizingMaskIntoConstraints = false
        mediaView.contentMode = .scaleAspectFill
        mediaView.clipsToBounds = true
        adView.mediaView = mediaView
        adView.addSubview(mediaView)

        let bottomOverlay = UIView()
        bottomOverlay.translatesAutoresizingMaskIntoConstraints = false
        bottomOverlay.backgroundColor = Self.scrim.withAlphaComponent(0.72)
        adView.addSubview(bottomOverlay)

        let headlineLabel = UILabel()
        headlineLabel.translatesAutoresizingMaskIntoConstraints = false
        headlineLabel.textColor = Self.onSurface
        headlineLabel.numberOfLines = 2
        headlineLabel.font = .systemFont(ofSize: 13, weight: .semibold)
        headlineLabel.text = nativeAd.headline
        adView.headlineView = headlineLabel
        bottomOverlay.addSubview(headlineLabel)

        let attribution = UILabel()
        attribution.translatesAutoresizingMaskIntoConstraints = false
        attribution.text = "Pub"
        attribution.font = .systemFont(ofSize: 10, weight: .semibold)
        attribution.textColor = Self.onTertiaryContainer
        attribution.backgroundColor = Self.tertiaryContainer
        attribution.layer.cornerRadius = 2
        attribution.clipsToBounds = true
        attribution.textAlignment = .center
        adView.addSubview(attribution)

        NSLayoutConstraint.activate([
                                        mediaView.topAnchor.constraint(equalTo: adView.topAnchor),
                                        mediaView.leadingAnchor.constraint(equalTo: adView.leadingAnchor),
                                        mediaView.trailingAnchor.constraint(equalTo: adView.trailingAnchor),
                                        mediaView.bottomAnchor.constraint(equalTo: adView.bottomAnchor),

                                        bottomOverlay.leadingAnchor.constraint(equalTo: adView.leadingAnchor),
                                        bottomOverlay.trailingAnchor.constraint(equalTo: adView.trailingAnchor),
                                        bottomOverlay.bottomAnchor.constraint(equalTo: adView.bottomAnchor),
                                        bottomOverlay.heightAnchor.constraint(greaterThanOrEqualToConstant: 46),

                                        headlineLabel.topAnchor.constraint(equalTo: bottomOverlay.topAnchor, constant: 7),
                                        headlineLabel.leadingAnchor.constraint(equalTo: bottomOverlay.leadingAnchor, constant: 8),
                                        headlineLabel.trailingAnchor.constraint(equalTo: bottomOverlay.trailingAnchor, constant: -8),
                                        headlineLabel.bottomAnchor.constraint(equalTo: bottomOverlay.bottomAnchor, constant: -7),

                                        attribution.topAnchor.constraint(equalTo: adView.topAnchor, constant: 4),
                                        attribution.leadingAnchor.constraint(equalTo: adView.leadingAnchor, constant: 4),
                                        attribution.widthAnchor.constraint(equalToConstant: 28),
                                        attribution.heightAnchor.constraint(equalToConstant: 16),
                                    ])

        adView.nativeAd = nativeAd
        return adView
    }

    private static let surface = UIColor(red: 0.05, green: 0.05, blue: 0.09, alpha: 1.0)
    private static let onSurface = UIColor(red: 0.96, green: 0.95, blue: 0.98, alpha: 1.0)
    private static let scrim = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 1.0)
    private static let tertiaryContainer = UIColor(red: 0.36, green: 0.08, blue: 0.19, alpha: 1.0)
    private static let onTertiaryContainer = UIColor(red: 1.0, green: 0.85, blue: 0.9, alpha: 1.0)
}
