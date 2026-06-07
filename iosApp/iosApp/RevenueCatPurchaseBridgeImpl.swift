import Foundation
import ComposeApp
import RevenueCat

final class RevenueCatPurchaseBridgeImpl: NSObject, IosRevenueCatPurchaseBridge {

    func logIn(userId: String, completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void) {
        DispatchQueue.main.async {
            guard Self.isConfigured(completion: completion) else {
                return
            }
            AppDiagnostics.log("RevenueCat login requested")
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
            AppDiagnostics.log("RevenueCat logout requested")
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
            AppDiagnostics.log("RevenueCat purchase requested for entitlement=\(entitlement)")
            Purchases.shared.getOfferings { offerings, error in
                if let error = error {
                    AppDiagnostics.record(error, context: "RevenueCat getOfferings failed")
                    completion(kotlinBool(false), kotlinBool(false), error.localizedDescription, kotlinBool(false))
                    return
                }

                guard let package = Self.package(for: entitlement, in: offerings) else {
                    let message = Self.missingPackageMessage(entitlement: entitlement, offerings: offerings)
                    AppDiagnostics.record(message, domain: "RevenueCat")
                    completion(kotlinBool(false), kotlinBool(false), message, kotlinBool(false))
                    return
                }

                AppDiagnostics.log("RevenueCat package selected: \(Self.describe(package))")
                Purchases.shared.purchase(package: package) { _, customerInfo, error, userCancelled in
                    if userCancelled {
                        AppDiagnostics.log("RevenueCat purchase cancelled by user for entitlement=\(entitlement)")
                        completion(kotlinBool(false), kotlinBool(false), nil, kotlinBool(true))
                        return
                    }
                    if let error = error {
                        AppDiagnostics.record(error, context: "RevenueCat purchase failed for entitlement=\(entitlement)")
                        completion(kotlinBool(false), kotlinBool(false), error.localizedDescription, kotlinBool(false))
                        return
                    }
                    AppDiagnostics.log("RevenueCat purchase completed for entitlement=\(entitlement)")
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
            AppDiagnostics.log("RevenueCat restore requested")
            Purchases.shared.restorePurchases { customerInfo, error in
                Self.complete(customerInfo: customerInfo, error: error, completion: completion)
            }
        }
    }

    private static func isConfigured(
        completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?) -> Void
    ) -> Bool {
        guard Purchases.isConfigured else {
            AppDiagnostics.record("RevenueCat bridge called before Purchases.configure()", domain: "RevenueCat")
            completion(kotlinBool(false), kotlinBool(false), "RevenueCat is not configured. Set REVENUECAT_APPSTORE_SDK_KEY.")
            return false
        }
        return true
    }

    private static func isConfigured(
        completion: @escaping @Sendable (KotlinBoolean, KotlinBoolean, String?, KotlinBoolean) -> Void
    ) -> Bool {
        guard Purchases.isConfigured else {
            AppDiagnostics.record("RevenueCat purchase called before Purchases.configure()", domain: "RevenueCat")
            completion(
                kotlinBool(false),
                kotlinBool(false),
                "RevenueCat is not configured. Set REVENUECAT_APPSTORE_SDK_KEY.",
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
            AppDiagnostics.record(error, context: "RevenueCat customerInfo operation failed")
            completion(kotlinBool(false), kotlinBool(false), error.localizedDescription)
            return
        }
        AppDiagnostics.log(
            "RevenueCat customerInfo updated: removeAds=\(hasEntitlement(customerInfo, IosRevenueCatPurchaseManager.companion.ENTITLEMENT_REMOVE_ADS)) chatPremium=\(hasEntitlement(customerInfo, IosRevenueCatPurchaseManager.companion.ENTITLEMENT_CHAT_PREMIUM))"
        )
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
        if entitlement == IosRevenueCatPurchaseManager.companion.ENTITLEMENT_CHAT_PREMIUM {
            return offerings?.offering(identifier: entitlement)?.availablePackages.first
                ?? fallbackPackage(for: entitlement, in: offerings)
        }

        if entitlement == IosRevenueCatPurchaseManager.companion.ENTITLEMENT_REMOVE_ADS {
            return offerings?.current?.availablePackages.first
                ?? fallbackPackage(for: entitlement, in: offerings)
        }

        return fallbackPackage(for: entitlement, in: offerings)
    }

    private static func fallbackPackage(for entitlement: String, in offerings: Offerings?) -> Package? {
        allPackages(in: offerings).first {
            $0.identifier.localizedCaseInsensitiveContains(entitlement) ||
                $0.storeProduct.productIdentifier.localizedCaseInsensitiveContains(entitlement)
        }
    }

    private static func allPackages(in offerings: Offerings?) -> [Package] {
        (offerings?.current?.availablePackages ?? []) +
            (offerings?.all.values.flatMap {
                $0.availablePackages
            } ?? [])
    }

    private static func missingPackageMessage(entitlement: String, offerings: Offerings?) -> String {
        let packages = allPackages(in: offerings).map {
            describe($0)
        }
        .joined(separator: ", ")
        let offeringIds = offerings?.all.keys.sorted().joined(separator: ", ") ?? ""
        return "No RevenueCat package configured for entitlement=\(entitlement). offerings=[\(offeringIds)] packages=[\(packages)]"
    }

    private static func describe(_ package: Package) -> String {
        "\(package.identifier):\(package.storeProduct.productIdentifier)"
    }
}

private func kotlinBool(_ value: Bool) -> KotlinBoolean {
    KotlinBoolean(bool: value)
}
