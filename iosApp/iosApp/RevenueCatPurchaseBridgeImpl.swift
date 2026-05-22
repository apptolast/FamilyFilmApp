import Foundation
import ComposeApp
import RevenueCat

final class RevenueCatPurchaseBridgeImpl: NSObject, IosRevenueCatPurchaseBridge {

    func logIn(userId: String, completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void) {
        DispatchQueue.main.async {
            guard Self.isConfigured(completion: completion) else {
                return
            }
            Purchases.shared.logIn(userId) { customerInfo, _, error in
                Self.complete(customerInfo: customerInfo, error: error, completion: completion)
            }
        }
    }

    func logOut(completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void) {
        DispatchQueue.main.async {
            guard Self.isConfigured(completion: completion) else {
                return
            }
            Purchases.shared.logOut { customerInfo, error in
                Self.complete(customerInfo: customerInfo, error: error, completion: completion)
            }
        }
    }

    func purchase(
        entitlement: String,
        completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?, KotlinBoolean) -> Void
    ) {
        DispatchQueue.main.async {
            guard Self.isConfigured(completion: completion) else {
                return
            }
            Purchases.shared.getOfferings { offerings, error in
                if let error = error {
                    completion(kotlinBool(false), kotlinBool(false), error.localizedDescription, kotlinBool(false))
                    return
                }

                guard let package = Self.package(for: entitlement, in: offerings) else {
                    completion(kotlinBool(false), kotlinBool(false), "No package configured for \(entitlement)", kotlinBool(false))
                    return
                }

                Purchases.shared.purchase(package: package) { _, customerInfo, error, userCancelled in
                    if userCancelled {
                        completion(kotlinBool(false), kotlinBool(false), nil, kotlinBool(true))
                        return
                    }
                    if let error = error {
                        completion(kotlinBool(false), kotlinBool(false), error.localizedDescription, kotlinBool(false))
                        return
                    }
                    completion(
                        kotlinBool(Self.hasEntitlement(customerInfo, IosRevenueCatPurchaseManager.companion.ENTITLEMENT_REMOVE_ADS)),
                        kotlinBool(Self.hasEntitlement(customerInfo, IosRevenueCatPurchaseManager.companion.ENTITLEMENT_CHAT_PREMIUM)),
                        nil,
                        kotlinBool(false)
                    )
                }
            }
        }
    }

    func restore(completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void) {
        DispatchQueue.main.async {
            guard Self.isConfigured(completion: completion) else {
                return
            }
            Purchases.shared.restorePurchases { customerInfo, error in
                Self.complete(customerInfo: customerInfo, error: error, completion: completion)
            }
        }
    }

    private static func isConfigured(
        completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void
    ) -> Bool {
        guard Purchases.isConfigured else {
            completion(kotlinBool(false), kotlinBool(false), "RevenueCat is not configured. Set REVENUECAT_APP_STORE_KEY.")
            return false
        }
        return true
    }

    private static func isConfigured(
        completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?, KotlinBoolean) -> Void
    ) -> Bool {
        guard Purchases.isConfigured else {
            completion(
                kotlinBool(false),
                kotlinBool(false),
                "RevenueCat is not configured. Set REVENUECAT_APP_STORE_KEY.",
                kotlinBool(false)
            )
            return false
        }
        return true
    }

    private static func complete(
        customerInfo: CustomerInfo?,
        error: Error?,
        completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void
    ) {
        if let error = error {
            completion(kotlinBool(false), kotlinBool(false), error.localizedDescription)
            return
        }
        completion(
            kotlinBool(hasEntitlement(customerInfo, IosRevenueCatPurchaseManager.companion.ENTITLEMENT_REMOVE_ADS)),
            kotlinBool(hasEntitlement(customerInfo, IosRevenueCatPurchaseManager.companion.ENTITLEMENT_CHAT_PREMIUM)),
            nil
        )
    }

    private static func hasEntitlement(_ customerInfo: CustomerInfo?, _ entitlement: String) -> Bool {
        customerInfo?.entitlements.active[entitlement] != nil
    }

    private static func package(for entitlement: String, in offerings: Offerings?) -> Package? {
        let packages = (offerings?.current?.availablePackages ?? []) +
            (offerings?.all.values.flatMap {
                $0.availablePackages
            } ?? [])

        return packages.first {
            $0.identifier.localizedCaseInsensitiveContains(entitlement)
        }
    }
}

private func kotlinBool(_ value: Bool) -> KotlinBoolean {
    KotlinBoolean(bool: value)
}
