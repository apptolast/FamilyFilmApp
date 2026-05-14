import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        // Start Koin before any composable mounts. Firebase / AdMob / RevenueCat
        // configuration is added in blocks 10, 14 and 15 of the migration plan
        // once the corresponding SPM packages have been resolved in Xcode.
        KoinInitKt.initKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
