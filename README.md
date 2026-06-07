![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/apptolast/FamilyFilmApp/build.yml)
![GitHub top language](https://img.shields.io/github/languages/top/apptolast/FamilyFilmApp)

![GitHub contributors](https://img.shields.io/github/contributors/apptolast/FamilyFilmApp)
![GitHub issues](https://img.shields.io/github/issues/apptolast/FamilyFilmApp)
![GitHub pull requests](https://img.shields.io/github/issues-pr/apptolast/FamilyFilmApp)


# FamilyFilmApp

### Description

This application solves the common problem of deciding what movies or TV series to watch when multiple people gather together. It's often difficult to reach consensus since someone has usually already seen what the majority wants to watch, or everyone has different preferences.

The app allows you to track movies you've watched and add ones to your watchlist. Our sophisticated algorithm filters this information across all group members and recommends movies that everyone will enjoy during your next movie night or family gathering.

# Architecture and Technologies

FamilyFilmApp ships on **Android and iOS** from a single Kotlin Multiplatform
codebase rooted at `composeApp/`. The Xcode project under `iosApp/` embeds the
KMP framework via the standard `:composeApp:embedAndSignAppleFrameworkForXcode`
Gradle task — there is no CocoaPods integration.

## Design Pattern
**MVVM + Clean Architecture**: stateful Screen + stateless Content + UiState +
ViewModel per feature in `commonMain`. ViewModels are platform-agnostic (no
`@HiltViewModel`, no Android types).

## State Management
**StateFlow + Coroutines** in commonMain. Composables collect with
`.collectAsStateWithLifecycle()`. UI state is exposed as immutable data
classes / sealed interfaces.

## Dependency Injection
**Koin** (BOM 4.2.1). Three modules: `dataModule`, `presentationModule`,
`platformModule` (the latter `expect`/`actual`). Constructor injection only;
`viewModelOf(::X)` plus `koinViewModel<X>()` in composables.

## Firebase (GitLive Multiplatform)
- **Auth** — email/password + Google OAuth (Credential Manager on Android,
  GoogleSignIn-iOS on iOS), email verification, account deletion
- **Firestore** — real-time `Flow` listeners; structure `FFA/{buildType}/users|groups|movies`
- **Functions** — `chatComplete` callable for the AI chat assistant
- **Analytics** — `Firebase.analytics.logEvent` in commonMain
- **Crashlytics** — release-only; commonMain wrapper at `firebase/CrashReporter.kt`
- **App Check** — `expect fun installAppCheckProvider`; PlayIntegrity on
  Android, AppAttest on iOS

## Main Libraries
- **Compose Multiplatform 1.10.3** — same UI runs on both platforms
- **Ktor Client 3.4** (OkHttp on Android, Darwin on iOS) — replaces Retrofit
- **Jetpack Navigation Compose MP 2.9** with `@Serializable` routes
- **Room KMP 2.8** + `sqlite-bundled` 2.6 — offline-first local DB
- **Coil 3.4** multiplatform images with the ktor3 network engine
- **kotlinx-coroutines / kotlinx-serialization / kotlinx-datetime**
- **multiplatform-settings 1.3** — SharedPreferences (Android) /
  NSUserDefaults (iOS) façade
- **BuildKonfig 6.0.9** — secrets from `local.properties` into commonMain
- **AdMob 25.2.0** on Android; iOS via SPM (`GoogleMobileAds`) — scaffold
- **RevenueCat 10.5.0** on Android; iOS via SPM (`RevenueCat`) — scaffold
- **CI/CD**: GitHub Actions; release pipelines driven by Fastlane and
  triggered by `v*` Git tags
- **ktlint** via [jlleitschuh](https://github.com/JLLeitschuh/ktlint-gradle)
  with Compose-specific rules from `io.nlopez.compose.rules:ktlint`

## How to participate?
### Fork the project
To participate in this project, we recommend:

*  **Fork** the project and give it a **star** to support and increase repo visibility.
*  **Create a branch** for developing improvements or new features. Why use a branch? It's better to develop changes in a separate branch to keep your fork clean, allowing you to pull updates we make to the main repository.
*  Merge changes into your branch, resolving any conflicts that may arise.
*  Create a **Pull Request** from your branch to our **develop** branch.
*  We will then review your PR, suggest changes, or accept it to merge your changes into the main repository.

### Configuration

The Kotlin Multiplatform module pulls every secret it needs from the
git-ignored `local.properties` at the project root, exposes them in
`commonMain` via [BuildKonfig](https://github.com/gmazzo/gradle-buildconfig-plugin),
and reads `composeApp/google-services.json` through the Google Services
plugin. Copy `local.properties.example` to `local.properties` and fill
the eight required keys before running Gradle:

| Key                              | Where to get it                                                          |
|----------------------------------|--------------------------------------------------------------------------|
| `sdk.dir`                        | Local Android SDK path (e.g. `~/Library/Android/sdk`)                    |
| `WEB_ID_CLIENT`                  | Firebase console → Authentication → Sign-in providers → Google → Web SDK |
| `TMDB_ACCESS_TOKEN`              | themoviedb.org → Settings → API → v3 bearer token                        |
| `ADMOB_APPLICATION_ID`           | AdMob console → App settings                                             |
| `ADMOB_BOTTOM_BANNER_ID`         | AdMob console → Ad units → bottom banner                                 |
| `ADMOB_APP_OPEN_ID`              | AdMob console → Ad units → app open                                      |
| `ADMOB_NATIVE_HOME_ID`           | AdMob console → Ad units → native (home)                                 |
| `REVENUECAT_PLAY_SDK_KEY`        | RevenueCat dashboard → Project settings → API keys (Play, prod)          |
| `REVENUECAT_PLAY_SDK_KEY_TEST`   | RevenueCat dashboard → Project settings → API keys (Play, sandbox)       |

Access them from Kotlin (`commonMain`) as
`com.apptolast.familyfilmapp.BuildConfig.TMDB_ACCESS_TOKEN`, etc.

#### Firebase

Create a Firebase project, register the Android app, add your SHA-1 key
under the General tab, download `google-services.json` and drop it into
`composeApp/`. iOS uses the same Firebase project: drag
`GoogleService-Info.plist` into `iosApp/iosApp/` from Xcode. In CI both
files are decoded from base64 secrets — see `.github/workflows/`.

#### Swift Package Manager (iOS only)

This project does **not** use CocoaPods. All iOS-native dependencies
come from SPM and are added once via Xcode:

1. Open `iosApp/iosApp.xcodeproj` in Xcode → File → Add Package Dependencies.
2. Add each of these repositories with the rule "Up to Next Major Version":

   | Package | URL | Products | Used by |
   |---------|-----|----------|---------|
   | Firebase iOS SDK | `https://github.com/firebase/firebase-ios-sdk.git` (11.x) | `FirebaseAuth`, `FirebaseFirestore`, `FirebaseFunctions`, `FirebaseAnalytics`, `FirebaseCrashlytics`, `FirebaseAppCheck`, `FirebaseCore` | GitLive Firebase wrappers (block 10) |
   | Google Mobile Ads | `https://github.com/googleads/swift-package-manager-google-mobile-ads.git` | `GoogleMobileAds` | AdMob native ads + adaptive banner (block 15 follow-up) |
   | User Messaging Platform | `https://github.com/googleads/swift-package-manager-google-user-messaging-platform.git` | `UserMessagingPlatform` | AdMob consent flow (block 15 follow-up) |
   | RevenueCat | `https://github.com/RevenueCat/purchases-ios-spm.git` | `RevenueCat` | Subscriptions (block 15 follow-up) |
   | GoogleSignIn-iOS | `https://github.com/google/GoogleSignIn-iOS.git` | `GoogleSignIn` | Google sign-in on iOS (block 15 follow-up) |

3. Drag `GoogleService-Info.plist` from your Firebase project into
   `iosApp/iosApp/` and make sure the file is in the `iosApp` target.

4. Update `iosApp/iosApp/Info.plist`:
   - `GADApplicationIdentifier` — your AdMob app id (same value as
     `ADMOB_APPLICATION_ID` in `local.properties`).
   - `GIDClientID` — your `WEB_ID_CLIENT` from `local.properties`.
   - `SKAdNetworkItems` — copy the dictionary list from
     [Google's reference page](https://developers.google.com/admob/ios/ios14).
   - `NSUserTrackingUsageDescription` is already filled in; tweak the
     copy to taste.

5. The Kotlin `ComposeApp` framework is embedded into the Xcode target
   via the Gradle task `:composeApp:embedAndSignAppleFrameworkForXcode`,
   invoked by an Xcode Build Phase Run Script before "Compile Sources" —
   no Podfile, no `pod install`.

#### iOS cinterop wiring (block 15 follow-up)

`iosMain` ships scaffolds that need cinterop bindings to the SPM
modules:

- [`IosGoogleSignInClient`](composeApp/src/iosMain/kotlin/com/apptolast/familyfilmapp/auth/IosGoogleSignInClient.kt)
- [`IosRevenueCatPurchaseManager`](composeApp/src/iosMain/kotlin/com/apptolast/familyfilmapp/purchases/IosRevenueCatPurchaseManager.kt)
- The `AdMobNativeAdManager` and adaptive banner — currently a no-op
  on iOS pending the same cinterop work.

Each class's KDoc lists the exact `.def` content you need to add
under `composeApp/src/nativeInterop/cinterop/` and the
`cinterops { ... }` block to register in
`composeApp/build.gradle.kts`'s iOS target. Until that wiring is in
place these screens render gracefully (the auth flow falls back to
email/password, paywall buttons resolve to "Cancelled", and the Home
screen renders without native ads).

#### What works on iOS today (after block 15)

| Feature | Status |
|---------|--------|
| Firestore / Auth / Functions / Analytics / Crashlytics | ✅ via GitLive (block 10) |
| App Check | ✅ DebugProvider in debug, AppAttest default in release (set from `iOSApp.swift`) |
| Email/password login + Google email-link verification | ✅ |
| Group CRUD, movie statuses, chat | ✅ |
| In-app review prompt | ✅ via `SKStoreReviewController` (block 15) |
| Google Sign-In | ⏳ scaffold — needs cinterop |
| RevenueCat paywall + restore | ⏳ scaffold — needs cinterop |
| AdMob banner + native ads | ⏳ no-op — needs cinterop |

## Building and running

```bash
# Android — debug build + unit tests
./gradlew :composeApp:assembleDebug :composeApp:testDebugUnitTest

# Android instrumented Compose tests (requires an emulator/device)
./gradlew :composeApp:connectedDebugAndroidTest

# iOS — open the Xcode project and Build/Run; Xcode invokes
# :composeApp:embedAndSignAppleFrameworkForXcode for you
open iosApp/iosApp.xcodeproj

# Auto-format before pushing
./gradlew :composeApp:ktlintFormat
```

JDK 17 is required (AGP 8.13.2 + Kotlin 2.3 toolchain). The Android target
ships JVM 17 bytecode; iOS uses arm64/simulator-arm64 frameworks.

## Deployment (Fastlane + GitHub Actions)

Releases are tag-driven: pushing a `vX.Y.Z` tag triggers both
`deploy-android.yml` and `deploy-ios.yml`. Both upload as **DRAFT**, so the
final rollout still requires a human click in Play Console / App Store
Connect.

| Platform | Lane (manual) | CI workflow | Behaviour |
|----------|---------------|-------------|-----------|
| Android  | `cd composeApp && bundle exec fastlane android internal` | — | Builds AAB, uploads to Internal testing as DRAFT |
| Android  | `cd composeApp && bundle exec fastlane android release_from_tag` | `.github/workflows/deploy-android.yml` | Tag → versionName, Play tracks max+1 → versionCode, uploads to Production as DRAFT |
| iOS      | `cd iosApp && bundle exec fastlane ios beta` | — | Builds .ipa, uploads to TestFlight |
| iOS      | `cd iosApp && bundle exec fastlane ios release_from_tag` | `.github/workflows/deploy-ios.yml` | Tag → CFBundleShortVersionString, latest+1 → CFBundleVersion, uploads to App Store Connect as DRAFT |

Local Fastlane setup:

1. `bundle install` (uses the root `Gemfile`).
2. Copy each `composeApp/fastlane/.env.example` and
   `iosApp/fastlane/.env.example` to `.env` and fill in the values
   (Play Console service account JSON, App Store Connect API key, match
   repo URL + passphrase, etc.).
3. Use the lanes above. CI relies on GitHub Actions Secrets instead of
   `.env` files; the full secret list is documented in
   `.github/workflows/deploy-android.yml` and `deploy-ios.yml`.

iOS signing is delegated to `match` (read-only on CI). Run
`bundle exec fastlane ios match_certificates` once locally to bootstrap
the signing repo.

## Development workflow

> **Jira is the source of truth.**

1. Create an `FFA-XXX` ticket in Jira.
2. Branch off `develop`: `feature/FFA-XXX-short-description`.
3. Commit using lowercase conventional commits, referencing `FFA-XXX`.
4. Open a PR targeting `develop` titled `FFA-XXX Description`.
5. Squash & rebase merge once approved.

Branch prefixes: `feature/`, `fix/`, `refactor/`, `test/`, `hotfix/`,
`release/`.

## Testing resources:
Koin Unit tests: https://insert-koin.io/docs/reference/koin-test/testing
Koin Android tests: https://insert-koin.io/docs/reference/koin-android/instrumented-testing/

## Contributors

### Android:
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-hgarciaalberto-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/hgarciaalberto)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-Coshiloco-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/Coshiloco)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-rndevelo-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/rndevelo)

### Backend:
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-TuColegaDev-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/TuColegaDev)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-Isabel9422-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/Isabel9422)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-El3auti-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/El3auti)

## You can find us at:

[<img alt="Discord" height="35" src="https://img.shields.io/badge/-Discord-7289DA?style=flat-square&amp;logo=discord&amp;logoColor=white"/>](https://discord.gg/wyPDmk6Fda)
<img alt="Twitch Status" height="35" src="https://img.shields.io/twitch/status/AndroidZen"/>


