import GoogleMobileAds
import UIKit

final class AppOpenAdManager: NSObject {

    static let shared = AppOpenAdManager()

    private var appOpenAd: AppOpenAd?
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Date?
    private var hasBeenBackgrounded = false

    private let adUnitId: String = (Bundle.main.object(forInfoDictionaryKey: "ADMobAppOpenUnitID") as? String) ?? ""

    override init() {
        super.init()
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appDidEnterBackground),
            name: UIApplication.didEnterBackgroundNotification,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appDidBecomeActive),
            name: UIApplication.didBecomeActiveNotification,
            object: nil
        )
    }

    @objc private func appDidEnterBackground() {
        hasBeenBackgrounded = true
    }

    @objc private func appDidBecomeActive() {
        if !hasBeenBackgrounded {
            if appOpenAd == nil && !isLoadingAd {
                loadAd()
            }
            return
        }
        showAdIfAvailable()
    }

    func loadAd() {
        guard !adUnitId.isEmpty, !isLoadingAd, !isAdAvailable() else {
            return
        }
        isLoadingAd = true
        Task {
            do {
                let ad = try await AppOpenAd.load(with: adUnitId, request: Request())
                self.appOpenAd = ad
                self.loadTime = Date()
                self.isLoadingAd = false
            } catch {
                self.isLoadingAd = false
            }
        }
    }

    func showAdIfAvailable() {
        if UserDefaults.standard.bool(forKey: "ads_removed") {
            return
        }
        guard !isShowingAd, isAdAvailable() else {
            if !isAdAvailable() {
                loadAd()
            }
            return
        }
        guard let root = rootViewController() else {
            return
        }
        isShowingAd = true
        appOpenAd?.fullScreenContentDelegate = self
        appOpenAd?.present(from: root)
    }

    private func isAdAvailable() -> Bool {
        guard appOpenAd != nil, let loadTime = loadTime else {
            return false
        }
        let fourHours: TimeInterval = 4 * 60 * 60
        if Date().timeIntervalSince(loadTime) > fourHours {
            appOpenAd = nil
            return false
        }
        return true
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
}

extension AppOpenAdManager: FullScreenContentDelegate {
    func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        appOpenAd = nil
        isShowingAd = false
        loadAd()
    }

    func ad(_ ad: FullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        appOpenAd = nil
        isShowingAd = false
        loadAd()
    }
}
