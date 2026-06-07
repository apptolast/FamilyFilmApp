# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FamilyFilmApp is a Kotlin Multiplatform (KMP) application — Android + iOS — that helps groups decide what movies and TV shows to watch together. It tracks watched media and watchlists across group members and uses a recommendation algorithm so everyone in the group has a good night.

**Tech stack (post-KMP migration, v1.1.0+)**:

| Concern | Choice |
|---------|--------|
| Language | Kotlin 2.3.21 multiplatform |
| UI | Compose Multiplatform 1.10.3 (Android + iOS) |
| Architecture | Clean Architecture + MVVM |
| DI | Koin BOM 4.2.1 (`koin-core`, `koin-compose`, `koin-compose-viewmodel`) |
| HTTP | Ktor Client 3.4.3 (OkHttp on Android, Darwin on iOS) |
| Persistence | Room KMP 2.8.4 + `androidx.sqlite:sqlite-bundled` 2.6.2 |
| Prefs | `multiplatform-settings` 1.3.0 (SharedPreferences / NSUserDefaults) |
| Navigation | Jetpack Navigation Compose Multiplatform 2.9.x, `@Serializable` routes |
| Firebase | GitLive 2.4.0 (Auth, Firestore, Functions, Analytics, Crashlytics) in commonMain |
| App Check | Native via `expect`/`actual` — PlayIntegrity on Android, AppAttest on iOS |
| Images | Coil 3.4 multiplatform + ktor3 network engine |
| Build secrets | BuildKonfig (`com.github.gmazzo.buildconfig` 6.0.9) — single source: `local.properties` |
| AdMob (Android) | `play-services-ads` 25.2.0, native ads + adaptive banner + app open |
| Subscriptions | RevenueCat 10.5.0 (Android), SPM `RevenueCat` (iOS, scaffold) |
| Tests | `kotlin-test` + Mokkery 3.3 in commonTest |
| iOS native deps | Swift Package Manager — no CocoaPods |
| Deployment | Fastlane + GitHub Actions (tag-driven) |
| AGP | 8.13.2 (JDK 17 required) |

## Important Guidelines for Claude Code

### Testing and Building

**DO NOT automatically run tests or build commands** after completing tasks unless explicitly requested by the user. The user will request test execution and builds when they consider it appropriate or will include it in specific task instructions.

Only run `./gradlew test` or `./gradlew build` when:
- The user explicitly asks for it
- The task instructions specifically include testing/building steps

### Code Formatting with ktlint

This project uses **ktlint** for code style enforcement. After completing any task that involves code changes:

1. **ALWAYS run** `./gradlew :composeApp:ktlintFormat` to auto-format the code
2. ktlint rules include Compose-specific linting via `io.nlopez.compose.rules:ktlint`
3. Baseline at `ktlint-baseline.xml` (regenerated empty after the KMP migration)

```bash
# After making code changes
./gradlew :composeApp:ktlintFormat

# Only if explicitly requested by user
./gradlew :composeApp:testDebugUnitTest
./gradlew :composeApp:assembleDebug
```

### Updating Release Notes (whatsnew files)

When the user requests **"Actualice los ficheros whatsnew"**:

1. **Review recent changes**: `git log --oneline -10` to see what's new
2. **Summarise in ≤3 lines**, user-facing language, prioritise impactful changes
3. **Update both language files** in `/distribution/whatsnew/`:
   - `whatsnew-en-GB` (English)
   - `whatsnew-es-ES` (Spanish)
4. **Maintain consistent tone** across languages

## Build Commands

### Setup Requirements

Before building, create `local.properties` in the project root with the keys
listed in `local.properties.example`:

```
sdk.dir=<absolute path to Android SDK>
WEB_ID_CLIENT=<google-web-client-id>
TMDB_ACCESS_TOKEN=<tmdb-v4-bearer>
ADMOB_APPLICATION_ID=<ca-app-pub-…>
ADMOB_BOTTOM_BANNER_ID=<…>
ADMOB_APP_OPEN_ID=<…>
ADMOB_NATIVE_HOME_ID=<…>
REVENUECAT_PLAY_SDK_KEY=<…>
REVENUECAT_PLAY_SDK_KEY_TEST=<…>
```

Drop your Firebase Android config at `composeApp/google-services.json` and the iOS one at `iosApp/iosApp/GoogleService-Info.plist`. Both files are gitignored.

See **README.md** for the full iOS SPM setup (Firebase iOS SDK, GoogleMobileAds, UserMessagingPlatform, GoogleSignIn-iOS, RevenueCat).

### Common Commands

```bash
# Build the Android debug APK
./gradlew :composeApp:assembleDebug

# Unit tests (commonTest runs as part of testDebugUnitTest on Android)
./gradlew :composeApp:testDebugUnitTest

# Instrumented Compose UI tests (requires emulator / device)
./gradlew :composeApp:connectedDebugAndroidTest

# Link KMP framework for iOS simulator
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Embed KMP framework into Xcode (invoked from Xcode Run Script Phase)
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode

# ktlint
./gradlew :composeApp:ktlintCheck
./gradlew :composeApp:ktlintFormat

# Release AAB (requires signing config in local.properties — see Fastlane)
./gradlew :composeApp:bundleRelease

# Clean
./gradlew clean
```

### Running Single Tests

```bash
# Single test class
./gradlew :composeApp:testDebugUnitTest --tests com.apptolast.familyfilmapp.ui.screens.home.HomeViewModelTest

# Single method
./gradlew :composeApp:testDebugUnitTest --tests com.apptolast.familyfilmapp.ui.screens.home.HomeViewModelTest.testSpecificMethod
```

### Running the iOS App

```bash
open iosApp/iosApp.xcodeproj
# Build & run from Xcode. The Run Script Phase invokes
# :composeApp:embedAndSignAppleFrameworkForXcode automatically before
# Compile Sources, so no extra Gradle command is needed.
```

## Architecture

### Directory Layout (KMP)

```
composeApp/
├── build.gradle.kts                # KMP targets, BuildKonfig, Room, signing
├── google-services.json            # gitignored
├── schemas/                        # Room exported schemas (versioned)
└── src/
    ├── commonMain/kotlin/com/apptolast/familyfilmapp/
    │   ├── analytics/              # AnalyticsTracker, AnalyticsEvents
    │   ├── ads/                    # NativeAdManager interface, AdaptiveBanner (expect)
    │   ├── ai/                     # GeminiChatService (calls Firebase Function)
    │   ├── auth/                   # GoogleSignInClient interface
    │   ├── di/                     # dataModule, presentationModule, platformModule (expect)
    │   ├── exceptions/             # CustomException
    │   ├── extensions/             # Misc Kotlin extensions
    │   ├── firebase/               # CrashReporter, AppCheckInitializer (expect), FirebaseUserMapper
    │   ├── model/
    │   │   ├── local/              # Domain models (Movie, Group, User, …)
    │   │   ├── remote/             # TMDB DTOs (@Serializable)
    │   │   └── room/               # Room entities (UserTable, MovieTable, …)
    │   ├── navigation/             # Routes (@Serializable), AppNavigation, NavigationAnalytics
    │   ├── network/                # TmdbApi, TmdbApiKtor, TmdbHttpClient, TmdbLocaleManager
    │   ├── purchases/              # PurchaseManager interface, PurchaseFailure
    │   ├── rating/                 # RateAppManager interface
    │   ├── repositories/
    │   │   ├── Repository.kt       # Interface — single source of truth for UI
    │   │   ├── ChatRepository.kt
    │   │   ├── FirebaseAuthRepository.kt
    │   │   └── datasources/        # Room, TMDB, Firestore datasources
    │   ├── room/                   # AppDatabase, DAOs, DatabaseBuilder (expect), converters
    │   ├── ui/
    │   │   ├── components/         # Reusable composables (BottomNavigationBar, CustomCard…)
    │   │   ├── screens/{feature}/  # Stateful Screen + stateless Content + UiState + ViewModel
    │   │   ├── sharedViewmodel/    # AuthViewModel
    │   │   └── theme/              # Color, Type, Theme (Material 3)
    │   └── utils/                  # DispatcherProvider, UsernameValidator, TmdbRegions
    ├── androidMain/kotlin/com/apptolast/familyfilmapp/
    │   ├── ads/                    # AdMobNativeAdManager, AdaptiveBanner.android
    │   ├── auth/                   # AndroidGoogleSignInClient (Credential Manager)
    │   ├── di/                     # platformModule.android.kt
    │   ├── firebase/               # AppCheckInitializer.android (PlayIntegrity)
    │   ├── platform/               # CurrentActivityHolder
    │   ├── purchases/              # RevenueCatPurchaseManager (real impl)
    │   ├── rating/                 # PlayInAppReviewManager
    │   ├── room/                   # DatabaseBuilder.android
    │   ├── FamilyFilmApp.kt        # Application (no @HiltAndroidApp)
    │   └── MainActivity.kt         # SplashScreen + ComposeUIViewController
    ├── androidMain/AndroidManifest.xml
    ├── androidMain/res/            # mipmap-*, xml/{backup_rules,network_security,…}
    ├── iosMain/kotlin/com/apptolast/familyfilmapp/
    │   ├── ads/                    # AdaptiveBanner.ios (no-op stub)
    │   ├── auth/                   # IosGoogleSignInClient (scaffold, needs cinterop)
    │   ├── di/                     # platformModule.ios.kt (Darwin engine, NSUserDefaults)
    │   ├── firebase/               # AppCheckInitializer.ios (Swift handles it)
    │   ├── purchases/              # IosRevenueCatPurchaseManager (scaffold)
    │   ├── rating/                 # StoreKitRateAppManager (real, SKStoreReviewController)
    │   ├── room/                   # DatabaseBuilder.ios (NSDocumentDirectory)
    │   ├── network/                # Locale.ios (NSLocale)
    │   └── MainViewController.kt   # initKoin + ComposeUIViewController
    └── commonResources/
        ├── values/strings.xml
        ├── values-es/strings.xml
        ├── drawable/
        └── font/
iosApp/
├── iosApp.xcodeproj
├── iosApp/
│   ├── iOSApp.swift                # FirebaseApp.configure() + Koin init
│   ├── Info.plist                  # GADApplicationIdentifier, GIDClientID, ATT, SKAdNetwork
│   ├── ContentView.swift
│   ├── GoogleService-Info.plist    # gitignored
│   └── Assets.xcassets/AppIcon.appiconset/
├── Configuration/Config.xcconfig
└── fastlane/
composeApp/fastlane/                # Android lanes
└── Fastfile, Appfile, .env.example
.github/workflows/                  # build.yml + deploy-android.yml + deploy-ios.yml
```

### MVVM Pattern

```
ui/screens/{feature}/
├── {Feature}ViewModel.kt    # Koin viewModelOf — no annotations
├── {Feature}UiState.kt      # Data class / sealed interface
├── {Feature}Screen.kt       # Stateful — collects VM state, supplies callbacks
└── {Feature}Content.kt      # (optional) Stateless — pure UI + @Preview
```

**Conventions** (carry over from pre-migration):
- UI state via `MutableStateFlow` exposed as `StateFlow`
- `.collectAsStateWithLifecycle()` in composables to avoid memory leaks
- `.update { }` for thread-safe state writes
- Sealed interfaces for distinct UI states (`Loading`, `Success`, `Error`)
- Every stateless `Content`/`Screen` and every reusable `components/` composable MUST have a `@Preview` wrapped in `FamilyFilmTheme`

### Data Layer (3-Tier)

Models live in three shapes with extension-function converters:

- **Remote** (`model/remote/`): TMDB DTOs annotated `@Serializable`, e.g. `TmdbMovieRemote`
- **Domain** (`model/local/`): Business models, e.g. `Movie`, `Group`, `User`
- **Room** (`model/room/`): Entities annotated with `@Entity`, e.g. `MovieTable`

Converters: `.toDomain()`, `.toRoom()`, `.toRemote()`.

### Repository Pattern

The repository layer mediates three datasources:

- **TmdbDatasource** — Ktor calls to api.themoviedb.org
- **RoomDatasource** — local SQLite via Room KMP
- **FirebaseDatabaseDatasource** — Firestore real-time sync (GitLive)

All three are injected via Koin. The single `Repository` interface is the only abstraction the UI sees.

### Firebase + Room Sync

Offline-first with a **write-through cache** mediated by the repository. Room is the single source of truth for the UI; Firestore syncs in the background.

> **Full details**: see the `firebase-specialist` agent at `.claude/agents/firebase-specialist.md`.

**Rules**:
- ViewModels observe Room `Flow`s — never Firebase directly
- Writes hit Firebase first, then Room (write-through)
- `startSync()` in ViewModel init, `stopSync()` in `onCleared()`
- Firebase ↔ Room datasources have no direct dependency on each other
- The repository is the only layer that knows both exist

### Dependency Injection (Koin)

Koin modules live in `commonMain/.../di/`:

- **`dataModule`** — `TmdbHttpClient`, `TmdbApiKtor`, datasources, repositories, `AnalyticsTracker`, `CrashReporter`, `GeminiChatService`
- **`presentationModule`** — all `viewModelOf(::XxxViewModel)` definitions
- **`platformModule`** — `expect val` in commonMain, `actual val` per platform:
  - androidMain: `Context`, `CoroutineScope`, `DispatcherProvider`, `AppDatabase` builder, `RevenueCatPurchaseManager`, `AdMobNativeAdManager`, `AndroidGoogleSignInClient`, `PlayInAppReviewManager`, `CurrentActivityHolder`
  - iosMain: same shape with iOS-native impls (Darwin engine, NSUserDefaults, `StoreKitRateAppManager`, scaffolds for GoogleSignIn/RevenueCat/AdMob)

Initialization: each platform calls `initKoin()` exactly once.
- Android: `initKoin { androidContext(this@FamilyFilmApp) }` from `FamilyFilmApp.onCreate()`
- iOS: `KoinInitKt.initKoinForIos()` from `iOSApp.swift`'s `init()`

### Navigation

- **Type**: Jetpack Compose Navigation Multiplatform, type-safe `@Serializable` routes
- **Location**: `commonMain/.../navigation/`
- **Routes** (`Routes.kt`): `Login`, `Home`, `Discover`, `Chat`, `Groups`, `Profile`, `Details(mediaId, mediaType)`
- **Pattern**: Single Compose entry point per platform

### Shared State

`AuthViewModel` in `commonMain/.../ui/sharedViewmodel/` provides authentication state across the entire app. It's resolved via `koinViewModel()` inside `AppNavigation`.

## Testing

- **Unit tests**: `composeApp/src/commonTest/` — Mokkery 3.3 for mocking, `kotlin-test` for assertions, `kotlinx-coroutines-test` + `Dispatchers.setMain()` instead of the old `MainDispatcherRule`. Example: `HomeViewModelTest.kt`.
- **Instrumented tests**: `composeApp/src/androidInstrumentedTest/` — Compose UI tests with Koin test modules instead of Hilt
- **Test runner**: standard `AndroidJUnitRunner` (no more `CustomHiltTestRunner`)

## Code Style

### ktlint

- **Version**: bundled via the `org.jlleitschuh.gradle.ktlint` plugin
- **Rules**: Compose-specific linting via `io.nlopez.compose.rules:ktlint`
- **Baseline**: `ktlint-baseline.xml` (empty after the KMP migration — regenerate with `./gradlew :composeApp:ktlintBaseline` if you need to suppress new findings)
- **Auto-format**: `./gradlew :composeApp:ktlintFormat` before committing

### Conventions

- **Code comments**: English
- **UI-facing strings**: Spanish (target audience) — externalised via `composeResources/values/strings.xml` (default English) and `values-es/strings.xml`
- **Logging**: `Firebase.crashlytics.log(…)` via the `CrashReporter` wrapper — no Kermit, no Timber
- **Error handling**: `CustomException` sealed class for domain errors
- **Async**: coroutines + `Flow` everywhere; GitLive Firebase exposes `Flow` directly

## Firebase Integration

> Use the `firebase-specialist` agent for architecture diagrams and MCP operations.

- **Auth** (`FirebaseAuthRepository`): GitLive `Firebase.auth` — email/password, Google OAuth, email verification (callbackFlow polling), account deletion
- **Firestore** (`FirebaseDatabaseDatasource`): GitLive `Firebase.firestore`, real-time `Flow` listeners. Structure: `FFA/{buildType}/users|groups|movies`
- **Functions** (`GeminiChatService`): `Firebase.functions.httpsCallable("chatComplete")`
- **Analytics** (`AnalyticsTracker`): commonMain wrapper around `Firebase.analytics.logEvent`
- **Crashlytics** (`CrashReporter`): commonMain wrapper around `Firebase.crashlytics.log/recordException/setUserId`. Enabled in release, disabled in debug.
- **App Check**: `expect fun installAppCheckProvider(debug: Boolean)` — PlayIntegrity on Android, AppAttest on iOS (latter wired in Swift via SPM)
- **MCP**: Firebase MCP server configured for direct Firestore/Auth queries from Claude Code

## iOS Integration

> Long-form steps and follow-up cinterop wiring live in **README.md**. Highlights:

- iOS deployment target 16.0
- Swift Package Manager is the only iOS dependency manager — **no CocoaPods**
- KMP framework embedded into Xcode via the `:composeApp:embedAndSignAppleFrameworkForXcode` Gradle task, called from an Xcode Build Phase Run Script before Compile Sources
- Firebase configured natively in `iOSApp.swift` via `FirebaseApp.configure()`
- `iosMain` ships scaffolds for `IosGoogleSignInClient`, `IosRevenueCatPurchaseManager`, and the AdMob banner — each class's KDoc lists the `.def` and `cinterops { }` wiring still to do

## Jira Integration

> Use the `jira-specialist` agent at `.claude/agents/jira-specialist.md`.

- **Instance**: `apptolast.atlassian.net`
- **Project Key**: `FFA`
- **Board**: FFA board (id: 1)
- **MCP**: Atlassian MCP server configured

## GitHub Integration

> Use the `github-specialist` agent at `.claude/agents/github-specialist.md`.

- **Organization**: `apptolast`
- **Repository**: `apptolast/FamilyFilmApp`
- **Default branch**: `develop`
- **MCP**: GitHub MCP server configured

## Development Workflow

> **Jira is the source of truth** for task tracking.

1. **Create Jira ticket** → `FFA-XXX`
2. **Branch from `develop`**: `feature/FFA-XXX-short-description`
3. **Commit** with `FFA-XXX` reference (conventional commits, lowercase)
4. **PR to `develop`** titled `FFA-XXX Description`
5. **Code review** → approve
6. **Squash & rebase** to keep `develop` linear
7. **Delete branch**, update local `develop`

**Branch types**: `feature/`, `fix/`, `refactor/`, `test/`, `hotfix/`, `release/`

## CI/CD

### GitHub Actions

- **`.github/workflows/build.yml`** — PR + push to `develop`. Three parallel jobs:
  - `ktlint` (Ubuntu)
  - `android-build`: `:composeApp:assembleDebug :composeApp:testDebugUnitTest`
  - `ios-build` (macOS-14): pre-links KMP framework + `xcodebuild -sdk iphonesimulator`
- **`.github/workflows/deploy-android.yml`** — `v*` tag triggered. Decodes keystore + Play service account + google-services.json, renders `local.properties`, runs `bundle exec fastlane android release_from_tag` from `composeApp/`. Uploads to Production track as DRAFT.
- **`.github/workflows/deploy-ios.yml`** — `v*` tag triggered, macOS-14. Pulls signing artifacts via `match` (read-only), `bundle exec fastlane ios release_from_tag` from `iosApp/`. Uploads to App Store Connect as DRAFT.

### Secrets

Build (always required): `WEB_ID_CLIENT`, `TMDB_ACCESS_TOKEN`, `ADMOB_APPLICATION_ID`, `ADMOB_BOTTOM_BANNER_ID`, `ADMOB_APP_OPEN_ID`, `ADMOB_NATIVE_HOME_ID`, `REVENUECAT_PLAY_SDK_KEY`, `REVENUECAT_PLAY_SDK_KEY_TEST`, `REVENUECAT_APPSTORE_SDK_KEY` (iOS).

Firebase config: `GOOGLE_SERVICES_JSON_BASE64` (Android), `IOS_GOOGLE_SERVICE_INFO_PLIST_BASE64`.

Android signing/release: `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`, `PLAY_SERVICE_ACCOUNT_JSON_BASE64`.

iOS release: `APP_STORE_CONNECT_API_KEY_ID`, `APP_STORE_CONNECT_API_KEY_ISSUER_ID`, `APP_STORE_CONNECT_API_KEY_BASE64`, `MATCH_GIT_URL`, `MATCH_PASSWORD`, `MATCH_GIT_BASIC_AUTHORIZATION`, `FASTLANE_TEAM_ID`, `FASTLANE_ITC_TEAM_ID`.

### Fastlane

Per-platform `fastlane/` directories (Bundler walks up to the root `Gemfile`):

- `composeApp/fastlane/Fastfile`: `android version`, `validate_play`, `build`, `internal`, `release_from_tag`. Tag → versionName, Play tracks max+1 → versionCode, injected via `-PappVersionCode=N -PappVersionName=X`.
- `iosApp/fastlane/Fastfile`: `ios match_certificates`, `build`, `beta`, `release_from_tag`. Tag → CFBundleShortVersionString, App Store Connect latest+1 → CFBundleVersion. `match` (read-only) syncs signing.

Local secrets: copy `.env.example` to `.env` in each fastlane directory. CI uses job-level env vars directly.

## Adding New Features

### Adding a Screen

1. Create `commonMain/.../ui/screens/{feature}/`
2. Create `{Feature}ViewModel` (no annotations — Koin handles it)
3. Create `{Feature}UiState` data class / sealed interface
4. Create `{Feature}Screen.kt` (stateful) + optional `{Feature}Content.kt` (stateless + `@Preview`)
5. Add `@Serializable data object/class` to `Routes.kt`
6. Wire `composable<Routes.X>` in `AppNavigation.kt`
7. Add `viewModelOf(::{Feature}ViewModel)` to `presentationModule`

### Adding API Endpoints

1. Add the endpoint to `network/TmdbApi.kt`
2. Implement in `network/TmdbApiKtor.kt`
3. Expose through the `Repository` interface (`repositories/Repository.kt`) and its impl
4. Call from the ViewModel via the injected repository

### Adding Database Entities

1. Create entity in `model/room/`
2. Create DAO under `room/`
3. Register in `AppDatabase.kt` (`@Database(entities = [...])`)
4. Bump version + add a `Migration` if existing installs are affected
5. Provide DAO in `platformModule` (the database singleton is in `platformModule`, the DAOs are extracted in `dataModule`)
6. Implement datasource access in `repositories/datasources/RoomDatasource.kt`

## Important Notes

### Build Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **JVM target**: 17 (Android + JVM main)
- **iOS deployment target**: 16.0
- **AGP**: 8.13.2
- **Kotlin**: 2.3.21
- **Compose Multiplatform**: 1.10.3
- **Gradle**: 8.10+ (JDK 17 required)

### ProGuard

Release builds use ProGuard with minification and resource shrinking enabled. Rules in `composeApp/proguard-rules.pro`.

### Room Schema

Schemas exported to `composeApp/schemas/` (version controlled).

### Background Work

Background sync runs as real-time Firebase listeners exposed as Flows — WorkManager has been removed. The repository handles sync lifecycle.

### Paging

Movie lists use Jetpack Paging 3 (`MoviePagingSource`).
