import UIKit
import ComposeApp
import AuthenticationServices
import CryptoKit

final class AppleSignInBridgeImpl: NSObject, IosAppleSignInBridge {

    private var pendingCompletion: ((String?, String?, String?, String?, String?) -> Void)?
    private var currentNonce: String?

    // Retained so ASAuthorizationController isn't deallocated before the delegate
    // fires. Apple's framework holds onto it internally too, but on iOS 26
    // simulators we've seen the delegate skipped when the local variable goes
    // out of scope; this belt-and-braces makes it deterministic.
    private var currentController: ASAuthorizationController?

    func startSignIn(completion: @escaping (String?, String?, String?, String?, String?) -> Void) {
        DispatchQueue.main.async {
            self.pendingCompletion = completion

            let nonce = Self.randomNonceString()
            self.currentNonce = nonce

            let provider = ASAuthorizationAppleIDProvider()
            let request = provider.createRequest()
            request.requestedScopes = [.fullName, .email]
            request.nonce = Self.sha256(nonce)

            let controller = ASAuthorizationController(authorizationRequests: [request])
            controller.delegate = self
            controller.presentationContextProvider = self
            self.currentController = controller
            NSLog("[AppleSignIn] performRequests dispatched")
            controller.performRequests()
        }
    }

    // Apple's recommended nonce generation.
    private static func randomNonceString(length: Int = 32) -> String {
        precondition(length > 0)
        let charset: [Character] = Array(
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._"
        )
        var result = ""
        var remaining = length
        while remaining > 0 {
            var random: UInt8 = 0
            let status = SecRandomCopyBytes(kSecRandomDefault, 1, &random)
            guard status == errSecSuccess else {
                continue
            }
            if random < (UInt8.max - (UInt8.max % UInt8(charset.count))) {
                result.append(charset[Int(random) % charset.count])
                remaining -= 1
            }
        }
        return result
    }

    private static func sha256(_ input: String) -> String {
        let data = Data(input.utf8)
        return SHA256.hash(data: data)
        .map {
            String(format: "%02x", $0)
        }
        .joined()
    }
}

extension AppleSignInBridgeImpl: ASAuthorizationControllerDelegate {

    func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization authorization: ASAuthorization
    ) {
        defer {
            pendingCompletion = nil
            currentNonce = nil
            currentController = nil
        }
        NSLog("[AppleSignIn] didCompleteWithAuthorization")
        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential,
              let nonce = currentNonce,
              let tokenData = appleIDCredential.identityToken,
              let idToken = String(data: tokenData, encoding: .utf8)
        else {
            NSLog("[AppleSignIn] credential cast / nonce / idToken extraction failed")
            pendingCompletion?(nil, nil, nil, nil, "Invalid Apple credential")
            return
        }
        let fullName = [
            appleIDCredential.fullName?.givenName,
            appleIDCredential.fullName?.familyName,
        ]
        .compactMap {
            $0
        }
        .joined(separator: " ")
        .nilIfEmpty()
        let authorizationCode = appleIDCredential.authorizationCode
            .flatMap {
                String(data: $0, encoding: .utf8)
            }
        pendingCompletion?(idToken, nonce, fullName, authorizationCode, nil)
    }

    func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError error: Error
    ) {
        defer {
            pendingCompletion = nil
            currentNonce = nil
            currentController = nil
        }
        let nsError = error as NSError
        NSLog("[AppleSignIn] didCompleteWithError domain=\(nsError.domain) code=\(nsError.code) description=\(error.localizedDescription) userInfo=\(nsError.userInfo)")
        if nsError.code == ASAuthorizationError.canceled.rawValue {
            pendingCompletion?(nil, nil, nil, nil, nil)
        } else {
            pendingCompletion?(nil, nil, nil, nil, error.localizedDescription)
        }
    }
}

extension AppleSignInBridgeImpl: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        UIApplication.shared
        .connectedScenes
        .compactMap {
            ($0 as? UIWindowScene)?.keyWindow
        }
        .first ?? ASPresentationAnchor()
    }
}

private extension String {
    func nilIfEmpty() -> String? {
        isEmpty ? nil : self
    }
}
