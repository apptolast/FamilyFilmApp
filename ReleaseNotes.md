# Release Notes

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
