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

## Design Pattern
**MVVM** (Model-View-ViewModel): Follows Google's established standards and is widely adopted by the developer community. This ensures clear separation between UI logic, business logic, and the data model, facilitating maintenance and scalability.

## State Management
**Flow and Coroutines**: Used for reactive state management, Flow enables efficient and safe UI updates in response to data changes.

## Dependency Injection
**Hilt**: Dependency injection is managed through Hilt, simplifying object construction and promoting loose coupling with more testable code.

## Firebase
**Firestore**: Used to manage users, movies, and the groups they belong to.

**Auth**: Firebase **Authentication** is used to obtain the UID that is sent to the backend for user identification. The registration providers used are: **email/password** and **Google**

**Crashlytics**: Used for early detection of application errors, allowing us to address them quickly and prevent them from affecting more users.

## Main Libraries
**Jetpack Compose**: This project is built using Jetpack Compose, Android's modern UI toolkit for creating native interfaces declaratively and efficiently. It simplifies and accelerates Android UI development.

**Retrofit**: Used for API calls due to its efficiency and easy integration with JSON converters like Gson.

**Navigation Component**: Navigation management uses the Android Jetpack navigation component, along with a [third-party library for managing navigation arguments](https://github.com/dilrajsingh1997/safe-compose-args).

**Coroutines**: Used for asynchronous programming and background task management with Kotlin Coroutines.

**StateFlow**: Used for communication between ViewModel and UI.

**CI/CD**: GitHub Actions is used for Continuous Integration.

**Ktlint**: Used for code checking and auto-formatting to maintain coding standards among all project contributors. We specifically use the plugin from [jlleitschuh](https://github.com/JLLeitschuh/ktlint-gradle).

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


