import GoogleMobileAds
import UIKit
import ComposeApp

final class IOSNativeAdLoader: NSObject, NativeAdLoader {

    private var loaders: [AdLoader] = []
    private var delegates: [NativeAdDelegate] = []

    func load(adUnitId: String, count: Int32, onLoaded: @escaping (Any) -> Void) {
        let options = MultipleAdsAdLoaderOptions()
        options.numberOfAds = Int(count)

        let delegate = NativeAdDelegate(onLoaded: { ad in onLoaded(ad) })
        delegates.append(delegate)

        let loader = AdLoader(
            adUnitID: adUnitId,
            rootViewController: rootViewController(),
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
        private let onLoaded: (NativeAd) -> Void

        init(onLoaded: @escaping (NativeAd) -> Void) {
            self.onLoaded = onLoaded
        }

        func adLoader(_ adLoader: AdLoader, didReceive nativeAd: NativeAd) {
            onLoaded(nativeAd)
        }

        func adLoader(_ adLoader: AdLoader, didFailToReceiveAdWithError error: Error) {
            // Silent — Kotlin observes the StateFlow; absent ads just keep the list short.
        }
    }
}

final class IOSNativeAdViewFactory: NSObject, NativeAdViewFactory {

    func createNativeAdView(handle: Any) -> UIView {
        guard let nativeAd = handle as? NativeAd else {
            return UIView()
        }

        let adView = NativeAdView()
        adView.translatesAutoresizingMaskIntoConstraints = false

        let mediaView = MediaView()
        mediaView.translatesAutoresizingMaskIntoConstraints = false
        mediaView.contentMode = .scaleAspectFill
        mediaView.clipsToBounds = true
        adView.mediaView = mediaView
        adView.addSubview(mediaView)

        let headlineLabel = UILabel()
        headlineLabel.translatesAutoresizingMaskIntoConstraints = false
        headlineLabel.textColor = .white
        headlineLabel.numberOfLines = 2
        headlineLabel.font = .systemFont(ofSize: 11, weight: .medium)
        headlineLabel.text = nativeAd.headline
        adView.headlineView = headlineLabel

        let gradient = GradientView()
        gradient.translatesAutoresizingMaskIntoConstraints = false
        gradient.isUserInteractionEnabled = false
        adView.addSubview(gradient)
        gradient.addSubview(headlineLabel)

        let attribution = UILabel()
        attribution.translatesAutoresizingMaskIntoConstraints = false
        attribution.text = "Pub"
        attribution.font = .systemFont(ofSize: 10, weight: .semibold)
        attribution.textColor = .black
        attribution.backgroundColor = UIColor(red: 1.0, green: 0.8, blue: 0.0, alpha: 1.0)
        attribution.layer.cornerRadius = 2
        attribution.clipsToBounds = true
        attribution.textAlignment = .center
        adView.addSubview(attribution)

        NSLayoutConstraint.activate([
                                        mediaView.topAnchor.constraint(equalTo: adView.topAnchor),
                                        mediaView.leadingAnchor.constraint(equalTo: adView.leadingAnchor),
                                        mediaView.trailingAnchor.constraint(equalTo: adView.trailingAnchor),
                                        mediaView.bottomAnchor.constraint(equalTo: adView.bottomAnchor),

                                        gradient.leadingAnchor.constraint(equalTo: adView.leadingAnchor),
                                        gradient.trailingAnchor.constraint(equalTo: adView.trailingAnchor),
                                        gradient.bottomAnchor.constraint(equalTo: adView.bottomAnchor),
                                        gradient.heightAnchor.constraint(equalToConstant: 56),

                                        headlineLabel.leadingAnchor.constraint(equalTo: gradient.leadingAnchor, constant: 6),
                                        headlineLabel.trailingAnchor.constraint(equalTo: gradient.trailingAnchor, constant: -6),
                                        headlineLabel.bottomAnchor.constraint(equalTo: gradient.bottomAnchor, constant: -8),

                                        attribution.topAnchor.constraint(equalTo: adView.topAnchor, constant: 4),
                                        attribution.leadingAnchor.constraint(equalTo: adView.leadingAnchor, constant: 4),
                                        attribution.widthAnchor.constraint(equalToConstant: 28),
                                        attribution.heightAnchor.constraint(equalToConstant: 16),
                                    ])

        adView.nativeAd = nativeAd
        return adView
    }
}

private final class GradientView: UIView {
    override class var layerClass: AnyClass {
        CAGradientLayer.self
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        let gl = layer as! CAGradientLayer
        gl.colors = [UIColor.clear.cgColor, UIColor.black.withAlphaComponent(0.75).cgColor]
        gl.startPoint = CGPoint(x: 0.5, y: 0.0)
        gl.endPoint = CGPoint(x: 0.5, y: 1.0)
    }

    required init?(coder: NSCoder) {
        fatalError()
    }
}
