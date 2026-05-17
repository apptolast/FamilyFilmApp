import UIKit
import ComposeApp
import AuthenticationServices
import CryptoKit

final class AppleSignInBridgeImpl: NSObject, IosAppleSignInBridge {

    private var pendingCompletion: ((String?, String?, String?, String?) -> Void)?
    private var currentNonce: String?

    func startSignIn(completion: @escaping (String?, String?, String?, String?) -> Void) {
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
        }
        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential,
              let nonce = currentNonce,
              let tokenData = appleIDCredential.identityToken,
              let idToken = String(data: tokenData, encoding: .utf8)
        else {
            pendingCompletion?(nil, nil, nil, "Invalid Apple credential")
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
        pendingCompletion?(idToken, nonce, fullName, nil)
    }

    func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError error: Error
    ) {
        defer {
            pendingCompletion = nil
            currentNonce = nil
        }
        let nsError = error as NSError
        if nsError.code == ASAuthorizationError.canceled.rawValue {
            // Treat cancellation as silent — no error message.
            pendingCompletion?(nil, nil, nil, nil)
        } else {
            pendingCompletion?(nil, nil, nil, error.localizedDescription)
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
