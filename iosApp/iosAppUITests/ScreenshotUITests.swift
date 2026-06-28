//
//  ScreenshotUITests.swift
//  iosAppUITests
//
//  Drives the app in demo/screenshot mode and captures App Store screenshots
//  for the five primary surfaces. Demo mode is requested via the launch
//  argument `-FFADemoMode YES`, which the Kotlin entry point (MainViewController)
//  reads from NSProcessInfo to swap in the offline FakeTmdbDatasource. That
//  datasource serves fictional titles whose posters are drawn from scratch in
//  Compose, so no real/copyrighted artwork or TMDB network image ever appears
//  (Apple Guideline 5.2.1).
//
//  Run via fastlane:  cd iosApp && bundle exec fastlane screenshots
//  (or directly:      cd iosApp && fastlane snapshot)
//
//  ── PREREQUISITE: AUTHENTICATION ─────────────────────────────────────────────
//  The app starts on Home only when a Firebase session exists; otherwise it
//  starts on Login. Demo mode swaps ONLY the movie/TV data source — it does not
//  bypass auth. For the navigation below to reach Home/Discover/Groups/Chat,
//  the simulator must already hold a signed-in session, OR a sign-in step must
//  be added at the top of `testCaptureScreenshots()` using a dedicated demo
//  account. See docs/screenshots-pipeline.md.
//
//  ── ACCESSIBILITY IDENTIFIERS ────────────────────────────────────────────────
//  The bottom-navigation items carry Compose testTags (TT_NAV_HOME, …). On iOS
//  these surface to XCUITest as accessibility identifiers when Compose's
//  accessibility tree is queried. If identifier lookup fails on your toolchain,
//  the helpers below fall back to (a) the localized nav label and (b) tapping
//  the tab bar by proportional coordinate. See the NOTE in
//  docs/screenshots-pipeline.md for testTags still recommended on each screen.
//

import XCTest

final class ScreenshotUITests: XCTestCase {

    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    @MainActor
    func testCaptureScreenshots() throws {
        let app = XCUIApplication()
        setupSnapshot(app)
        // Demo mode: offline fictional data + generated posters. OFF in production.
        app.launchArguments += ["-FFADemoMode", "YES"]
        app.launch()

        // Give Compose + the (mocked) data layer time to render the first frame.
        waitForAppReady(app)

        // 01 — Home (start destination when authenticated).
        selectTab(app, identifier: "nav_home", index: 0)
        snapshot("01_Home")

        // 02 — Discover.
        selectTab(app, identifier: "nav_discover", index: 1)
        snapshot("02_Discover")

        // 03 — Groups.
        selectTab(app, identifier: "nav_groups", index: 3)
        snapshot("03_Groups")

        // 04 — Chat.
        selectTab(app, identifier: "nav_chat", index: 2)
        snapshot("04_Chat")

        // 05 — Profile (reached from the Home top-bar profile action).
        selectTab(app, identifier: "nav_home", index: 0)
        openProfile(app)
        snapshot("05_Profile")
    }

    // MARK: - Navigation helpers

    /// Waits until the UI has settled enough to interact with.
    @MainActor
    private func waitForAppReady(_ app: XCUIApplication) {
        // The tab bar (or any nav item) appearing is the strongest signal we're
        // past the splash/login. Fall back to a fixed grace period otherwise.
        let homeTab = app.descendants(matching: .any).matching(identifier: "nav_home").firstMatch
        if !homeTab.waitForExistence(timeout: 30) {
            // Either auth gated us on Login, or identifiers aren't exposed.
            // Give the first frame a moment so coordinate taps still land.
            sleep(5)
        }
    }

    /// Selects a bottom-navigation tab. Tries the accessibility identifier first,
    /// then taps the tab bar by proportional coordinate as a robust fallback.
    @MainActor
    private func selectTab(_ app: XCUIApplication, identifier: String, index: Int) {
        let byId = app.descendants(matching: .any).matching(identifier: identifier).firstMatch
        if byId.waitForExistence(timeout: 3), byId.isHittable {
            byId.tap()
            sleep(1)
            return
        }

        // Coordinate fallback: split the bottom strip into 4 equal tabs and tap
        // the centre of the requested one. Order: Home, Discover, Chat, Groups.
        let tabCount = 4
        let window = app.windows.firstMatch
        let fractionX = (CGFloat(index) + 0.5) / CGFloat(tabCount)
        // ~6% up from the bottom keeps the tap inside the nav bar across devices.
        let coordinate = window.coordinate(withNormalizedOffset: CGVector(dx: fractionX, dy: 0.96))
        coordinate.tap()
        sleep(1)
    }

    /// Opens the Profile screen via the Home top-bar avatar action.
    @MainActor
    private func openProfile(_ app: XCUIApplication) {
        let byId = app.descendants(matching: .any).matching(identifier: "home_profile_topbar_action").firstMatch
        if byId.waitForExistence(timeout: 3), byId.isHittable {
            byId.tap()
            sleep(1)
            return
        }

        // Coordinate fallback: top-right action area of the Home top bar.
        let window = app.windows.firstMatch
        let coordinate = window.coordinate(withNormalizedOffset: CGVector(dx: 0.9, dy: 0.06))
        coordinate.tap()
        sleep(1)
    }
}
