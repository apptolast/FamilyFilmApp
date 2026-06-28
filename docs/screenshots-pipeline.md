# iOS App Store Screenshot Pipeline (fastlane snapshot + demo mode)

This pipeline generates App Store screenshots that show **fictional movies/TV shows
with original, Compose-drawn poster artwork** — no real/copyrighted posters and no
TMDB network images. It exists to satisfy Apple **Guideline 5.2.1**.

## How it works

1. The app is launched with the **launch argument `-FFADemoMode YES`**.
2. `MainViewController` (Kotlin/iosMain) reads `NSProcessInfo.processInfo.arguments`,
   detects the flag, and calls `initKoin(demoMode = true)`.
3. `initKoin` appends `demoModule`, which **overrides** the `TmdbDatasource` binding
   with `FakeTmdbDatasource` (commonMain). The fake datasource serves ~12 fictional
   titles (movies + TV shows) entirely offline.
4. Each fictional title's `posterPath` is the sentinel **`demo://<seed>`**.
5. The central `PosterImage` composable (`ui/components/PosterImage.kt`) detects the
   `demo://` prefix and draws an **original generated poster** (gradient + title +
   fake score badge + genre chip) using only Compose primitives — no bundled images,
   no network. For all normal posterPaths it behaves exactly as before.

Production is unaffected: the flag defaults to `false`, `demoModule` is never added,
and Android ignores the new `initKoin` parameter.

| Thing | Value |
|-------|-------|
| Launch argument | `-FFADemoMode YES` (also accepts a bare `FFA_DEMO_MODE`) |
| Demo poster sentinel | `demo://<seed>` in `Media.posterPath` (e.g. `demo://904`) |
| Central poster component | `composeApp/src/commonMain/.../ui/components/PosterImage.kt` |
| Fake datasource | `composeApp/src/commonMain/.../repositories/datasources/FakeTmdbDatasource.kt` |
| Demo Koin module | `demoModule` in `composeApp/src/commonMain/.../di/Modules.kt` |

## Set up the UI test target (one command — no manual Xcode steps)

The `iosAppUITests` UI Testing Bundle target is created **programmatically** with the
`xcodeproj` gem (bundled with fastlane), so you never touch Xcode's target wizard:

```bash
cd iosApp && bundle exec ruby scripts/setup_screenshots_target.rb
```

The script is **idempotent** (safe to re-run) and:

- adds the `iosAppUITests` UI Testing Bundle target (`TEST_TARGET_NAME = iosApp`,
  bundle id `com.apptolast.familyfilmapp.uitests`, auto-synthesized Info.plist);
- attaches `iosAppUITests/SnapshotHelper.swift` and `ScreenshotUITests.swift` to it;
- makes the test target depend on the app;
- registers it in the shared **`iosApp`** scheme's Test action.

> This target is committed to `iosApp.xcodeproj` once. After it exists, the script is a
> no-op and you only run `fastlane screenshots`. Verify with
> `xcodebuild -list -project iosApp/iosApp.xcodeproj` (should list `iosApp` and
> `iosAppUITests`).

## Running

```bash
cd iosApp
bundle exec fastlane screenshots     # preferred (links the simulator framework, then runs snapshot)
# or, reading the Snapfile directly (no framework pre-link):
fastlane snapshot
```

Output: `iosApp/fastlane/screenshots/<lang>/...png` (cleared on each run).

- **Devices**: `iPhone 17 Pro Max`, `iPad Pro 13-inch (M4)` (Snapfile).
- **Languages**: `en-GB`, `es-ES`.
- **Captured frames**: `01_Home`, `02_Discover`, `03_Groups`, `04_Chat`, `05_Profile`.

To upload the captured screenshots (no binary), use the existing
`bundle exec fastlane ios upload_store_assets` lane.

## Authentication: handled automatically (no demo account needed)

Demo mode **skips the Firebase login gate**. When `-FFADemoMode YES` is present:

- `ScreenshotMode.activate()` runs (`screenshot/ScreenshotMode.kt`), and `AuthViewModel`
  emits `AuthState.Authenticated(ScreenshotDemoData.user)` instead of observing Firebase.
  The app therefore starts on **Home** with the bottom nav, no login step required.
- The iOS host (`AppBootstrap` in `iOSApp.swift`) **skips the ATT / notification / UMP /
  AdMob bootstrap**, so no system permission dialog or ad appears in any shot.
- The demo user has `hasRemovedAds = true`, so ad slots are hidden too.

`Home` and `Discover` render the fictional titles + generated posters (these are the
IP-relevant screens). `Groups`, `Chat` and `Profile` show empty/default states because the
demo session has no synced Firebase data — fine for the store (they contain no third-party
artwork). If you want richer Groups/Profile shots, sign in once on the simulator with a
real demo account that owns a couple of groups, then run with demo mode for the fake posters.

## Accessibility identifiers

The bottom navigation now exposes stable Compose testTags, added in this change:

| Tab | testTag (`utils/TestConstants.kt`) |
|-----|-----------------------------------|
| Home | `nav_home` (`TT_NAV_HOME`) |
| Discover | `nav_discover` (`TT_NAV_DISCOVER`) |
| Chat | `nav_chat` (`TT_NAV_CHAT`) |
| Groups | `nav_groups` (`TT_NAV_GROUPS`) |
| Profile (top-bar action, pre-existing) | `home_profile_topbar_action` (`TT_HOME_PROFILE_TOPBAR_ACTION`) |

On iOS, Compose surfaces `testTag` to XCUITest as an accessibility identifier. The
test tries identifiers first and **falls back to proportional tab-bar coordinate taps**
if a toolchain doesn't expose them, so it works either way.

### NOTE — testTags still recommended for fully reliable navigation

For richer/more deterministic screenshots, consider adding identifiers later to:

- The **Profile** screen root and its key rows (currently only the top-bar entry has a tag).
- The **Chat** screen's send field / message list, so the Chat shot can be staged with a
  sample conversation.
- A screen-root testTag per screen (e.g. `screen_home`, `screen_discover`, `screen_groups`,
  `screen_chat`, `screen_profile`) to let the test assert it reached each surface before
  snapping, instead of relying on `sleep`.
