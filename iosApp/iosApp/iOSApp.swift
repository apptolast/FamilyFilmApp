import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseAppCheck

@main
struct iOSApp: App {
    init() {
        // Configure App Check BEFORE FirebaseApp.configure() so the first
        // Firestore/Auth/Functions calls go out with a valid attestation
        // token. In debug we use the SDK's Debug provider (prints a JWT
        // that you whitelist in the Firebase console); in release we leave
        // the default factory which uses AppAttest on iOS 14+.
        #if DEBUG
        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        #endif

        FirebaseApp.configure()

        // Start Koin after Firebase is configured so any singleton resolved
        // on first injection can safely touch Firestore/Auth.
        KoinInitKt.initKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
