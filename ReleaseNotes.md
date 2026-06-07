# Release Notes

## v1.1.0 — Kotlin Multiplatform migration

### Headline
- **iOS app**: the codebase now targets Android **and** iOS from a single
  Kotlin Multiplatform / Compose Multiplatform module (`composeApp/`).
  The legacy Android-only `app/` module is gone.

### Architecture
- Replaced **Hilt** with **Koin** (BOM 4.2.1) across the entire app
- Replaced **Retrofit** with **Ktor Client 3.4** (OkHttp on Android,
  Darwin on iOS)
- Replaced **MockK** with **Mokkery 3.3** in unit tests
- Migrated Firebase to **GitLive 2.4.0** (Auth, Firestore, Functions,
  Analytics, Crashlytics) running entirely from commonMain
- App Check kept as `expect`/`actual`: PlayIntegrity on Android, AppAttest on
  iOS (the only native bridge that survived the migration)
- Removed WorkManager — background sync was already real-time Firestore
  listeners
- Removed Timber + Kermit — logging now flows through a thin
  `CrashReporter` wrapper over `Firebase.crashlytics`

### Build & tooling
- Bumped to **AGP 8.13.2**, **Kotlin 2.3.21**, **Compose Multiplatform 1.10.3**
- JVM bytecode target raised to 17 (required by GitLive + `kotlin.uuid`)
- Build secrets now flow through **BuildKonfig** (`gmazzo` 6.0.9), exposing
  `local.properties` keys as constants in commonMain
- **Room KMP 2.8.4** + `sqlite-bundled` driver, KSP per Apple target
- iOS native dependencies migrated from **CocoaPods to Swift Package
  Manager**: Firebase iOS SDK, GoogleMobileAds, UserMessagingPlatform,
  GoogleSignIn-iOS, RevenueCat
- KMP framework embedded into Xcode via the standard
  `:composeApp:embedAndSignAppleFrameworkForXcode` Gradle task — no
  `cocoapods { }` block, no `Podfile`

### Deployment
- New **Fastlane** setup with per-platform lanes (`composeApp/fastlane/`
  for Android, `iosApp/fastlane/` for iOS). Tag-driven release model:
  pushing a `vX.Y.Z` tag derives versionName / CFBundleShortVersionString
  from the tag and computes the next versionCode / build number from the
  store at lane runtime.
- New GitHub Actions workflows: `build.yml` (PR validation across
  ktlint + Android + iOS-simulator), `deploy-android.yml` (Play
  Production as DRAFT), `deploy-ios.yml` (App Store Connect as DRAFT)
- iOS code signing delegated to `match` (read-only on CI)

### Known follow-ups
- iOS scaffolds need cinterop wiring before parity with Android is
  complete: Google Sign-In, RevenueCat purchases, AdMob native +
  adaptive banner. Each class's KDoc lists the `.def` and
  `cinterops { }` snippet still to add.

## v.0.5.1

### New Features
- Added one-time purchase to remove all ads permanently via RevenueCat
- Added App Open Ads on app foreground transitions
- Added unique username feature with real-time availability checking
- Added region selection in Profile for localized movie data
- Added media filter chips (Movies, TV Shows, All) on Home screen
- Added per-group movie and TV show watch status tracking
- Added TV Shows support alongside Movies throughout the app

### Bug Fixes
- Fixed Home screen filter chips overlapping with movie grid on scroll
- Fixed purchase loading dialog race condition with payment UI
- Fixed App Open Ads still showing after user purchased ad removal
- Fixed dependency version conflicts in CI build (coroutines, concurrent-futures)

### Improvements
- Redesigned Groups card: compact member list, inline action icons with tooltips
- Redesigned Profile screen with subscription section and restore purchases
- Modernized repository layer from callbacks to suspend functions
- Implemented differential sync for remotely deleted groups
- Added comprehensive unit and instrumented test coverage

## v.0.5.0

### New Features
- Added region selection in Profile for localized movie and provider data
- Added App Open Ads on app foreground transitions
- Added unique username feature with real-time availability checking
- Added per-group movie and TV show watch status tracking
- Added TV Shows support alongside Movies throughout the app
- Added media filter chips (Movies, TV Shows, All) on Home screen

### Bug Fixes
- Fixed dependency version conflicts in CI (coroutines, concurrent-futures)
- Fixed missing ADMOB_APP_OPEN_ID in deploy workflow

### Improvements
- Redesigned Groups card with compact member list
- Added comprehensive unit and instrumented test coverage
- Added UI test tags for instrumented tests

## v.0.4.2

### New Features
- Added movie recommendation algorithm per group
- Added Discovery screen for browsing new content
- Added Crashlytics integration and structured logging with Timber

### Bug Fixes
- Fixed group sync issues and fragile LIKE query in GroupDao
- Fixed Room database migration strategy
- Fixed write-through cache consistency in Repository
- Fixed hardcoded email and crashlytics configuration issues

### Improvements
- Full UI/UX redesign following Material Design 3 guidelines
- Modernized repository layer from callbacks to suspend functions
- Centralized sync lifecycle management in AuthViewModel
- Implemented differential sync for remotely deleted groups
- Added Room table indexes for better query performance
- Migrated token storage to EncryptedSharedPreferences

## v.0.4.1

### New Features
- Added AdMob banner ads on authenticated screens
- Added Spanish translations for entire app
- Added privacy policy screen
- Added Google Credential Manager for modern sign-in flow

### Bug Fixes
- Fixed Google Sign-In on latest Android versions
- Fixed login flow issues and production crashes
- Fixed TMDB movie language not matching user locale
- Fixed user ID vs UID inconsistency

### Improvements
- Refactored Home screen, Details screen, and Login UI
- Refactored TopAppBar and StatusBar for consistency
- Improved auth error handling and user-facing messages
- Updated deploy workflow and dependencies

## v.0.4.0

### New Features
- Migrated backend to Firebase (Auth + Firestore)
- Added version catalog for dependency management
- Implemented Discover feature for movie exploration

### Bug Fixes
- Fixed group creation and sync issues
- Fixed production base URL configuration

### Improvements
- Major dependency update and migration to version catalog
- Refactored Group Screen UI and logic
- Updated Home screen to integrate search functionality

## v.0.3.14

### New Features
- Added user profile avatar with vector image
- Implemented UI update solution when deleting the last group

### Bug Fixes
- Fixed UI not updating when the last group is deleted
- Fixed NoSuchElementException when removing users from a group

### Improvements
- Refactored ProfileScreen.kt for better maintainability
- Optimized navigation flow in DetailsScreen.kt
- Commented out notification section for future implementation

## v.0.3.13

### New Features
- Added movie provider integration
- Implemented streaming service availability indicators

### Bug Fixes
- Fixed crash when searching movies with special characters

### Improvements
- Enhanced movie detail screen with provider information

## v.0.3.12

### New Features
- Added Firebase Analytics for user behavior tracking
- Implemented deep linking for sharing movies

### Bug Fixes
- Fixed movie detail screen layout issues on smaller devices

### Improvements
- Reduced app size by optimizing resources

## v.0.3.11

### New Features
- Added multi-language support
- Implemented dark mode

### Bug Fixes
- Fixed image loading issues in movie lists

### Improvements
- Enhanced animations between screen transitions

## v.0.3.10

### New Features
- Added movie recommendation engine
- Implemented social sharing features

### Bug Fixes
- Fixed group synchronization issues with Firebase

### Improvements
- Optimized database queries for faster loading

## v.0.3.9

### New Features
- Added offline mode support
- Implemented Room database for local caching

### Bug Fixes
- Fixed authentication token renewal process

### Improvements
- Reduced network calls for better battery performance

## v.0.3.8

### New Features
- Added user notifications for group activities
- Implemented movie watch status tracking

### Bug Fixes
- Fixed UI inconsistencies across different Android versions

### Improvements
- Enhanced group management interface

## v.0.3.7

### New Features
- Added movie filtering by genre
- Implemented advanced search functionality

### Bug Fixes
- Fixed memory leaks in movie detail screens

### Improvements
- Improved error handling and user feedback

## v.0.3.6

### New Features
- Added user profile customization
- Implemented email verification system

### Bug Fixes
- Fixed group invitation flow issues

### Improvements
- Enhanced animation smoothness throughout the app

## v.0.3.5

### New Features
- Added group movie voting system
- Implemented movie watchlist sharing

### Bug Fixes
- Fixed data synchronization between devices

### Improvements
- Optimized image loading and caching

## v.0.3.4

### New Features
- Added movie rating system
- Implemented personal notes for movies

### Bug Fixes
- Fixed issues with user authentication persistence

### Improvements
- Enhanced movie detail information display

## v.0.3.3

### New Features
- Added movie search functionality
- Implemented movie detail screen

### Bug Fixes
- Fixed navigation issues between screens

### Improvements
- Added loading indicators for network operations

## v.0.3.2

### New Features
- Added group creation functionality
- Implemented user invitation system

### Bug Fixes
- Fixed UI layout issues on tablet devices

### Improvements
- Enhanced keyboard handling and focus management

## v.0.3.1

### New Features
- Added user authentication with Firebase
- Implemented basic navigation structure

### Bug Fixes
- Fixed startup crashes on certain devices

### Improvements
- Implemented responsive UI layouts

## v.0.3.0

### New Features
- Initial version with core functionality
- Group system implementation
- Movie status management (watched/to watch)

### Bug Fixes
- Initial system stability fixes

### Improvements
- First implementation of user interface
