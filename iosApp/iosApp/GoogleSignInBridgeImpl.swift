import UIKit
import ComposeApp
import GoogleSignIn

final class GoogleSignInBridgeImpl: NSObject, IosGoogleSignInBridge {

    func signIn(completion: @escaping (String?, String?, String?) -> Void) {
        DispatchQueue.main.async {
            guard let presenter = Self.topViewController() else {
                completion(nil, nil, "No presenter available")
                return
            }
            GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { result, error in
                if let error = error {
                    completion(nil, nil, error.localizedDescription)
                    return
                }
                let idToken = result?.user.idToken?.tokenString
                let accessToken = result?.user.accessToken.tokenString
                if idToken == nil {
                    completion(nil, nil, "Missing idToken")
                } else {
                    completion(idToken, accessToken, nil)
                }
            }
        }
    }

    func signOut() {
        GIDSignIn.sharedInstance.signOut()
    }

    private static func topViewController(
        base: UIViewController? = UIApplication.shared
        .connectedScenes
        .compactMap {
            ($0 as? UIWindowScene)?.keyWindow?.rootViewController
        }
        .first
    ) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
}
