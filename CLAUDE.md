# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FamilyFilmApp is an Android application that helps groups decide what movies to watch together. It tracks watched movies and watchlists across group members, using an algorithm to recommend movies everyone will enjoy.

**Tech Stack**: Kotlin, Jetpack Compose, MVVM, Hilt, Firebase (Auth/Firestore), Retrofit, Room, WorkManager

## Important Guidelines for Claude Code

### Testing and Building

**DO NOT automatically run tests or build commands** after completing tasks unless explicitly requested by the user. The user will request test execution and builds when they consider it appropriate or will include it in specific task instructions.

Only run `./gradlew test` or `./gradlew build` when:
- The user explicitly asks for it
- The task instructions specifically include testing/building steps

### Code Formatting with ktlint

This project uses **ktlint** for code style enforcement. After completing any task that involves code changes:

1. **ALWAYS run** `./gradlew ktlintFormat` to auto-format the code according to project standards
2. This ensures all code complies with the established style guidelines
3. ktlint rules include Compose-specific linting via `io.nlopez.compose.rules:ktlint`

**Example workflow:**
```bash
# After making code changes
./gradlew ktlintFormat

# Only if explicitly requested by user
./gradlew test
./gradlew build
```

### Updating Release Notes (whatsnew files)

When the user requests to **"Actualice los ficheros whatsnew"** (Update whatsnew files):

1. **Review recent changes**: Check the git commit history on the current branch to understand what features, fixes, or improvements have been implemented
2. **Create a brief summary**: Write a concise summary of the changes in **no more than 3 lines**
3. **Update all language files**: Update the whatsnew files for each supported language in `/distribution/whatsnew/`:
   - `whatsnew-en-GB` (English)
   - `whatsnew-es-ES` (Spanish)
   - Any other language files present

**Guidelines**:
- Keep the summary user-friendly and focused on user-facing changes
- Avoid technical jargon; focus on benefits to the user
- Prioritize the most impactful changes if there are many commits
- Maintain consistent tone across all language versions

**Example workflow:**
```bash
# Review recent commits
git log --oneline -10

# Update whatsnew files based on commits
# Then format if needed
./gradlew ktlintFormat
```

## Build Commands

### Setup Requirements

Before building, create `local.properties` in the project root with:
```
WEB_ID_CLIENT=<your-google-web-client-id>
TMDB_ACCESS_TOKEN=<your-tmdb-api-token>
ADMOB_APPLICATION_ID=<your-admob-app-id>
ADMOB_BOTTOM_BANNER_ID=<your-admob-banner-id>
```

Additionally, add `google-services.json` to the `app/` directory from your Firebase project.

### Common Commands

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run ktlint checks
./gradlew ktlintCheck

# Auto-format code with ktlint
./gradlew ktlintFormat

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

### Running Single Tests

```bash
# Run a single test class
./gradlew test --tests com.apptolast.familyfilmapp.ui.screens.home.HomeViewModelTest

# Run a single test method
./gradlew test --tests com.apptolast.familyfilmapp.ui.screens.home.HomeViewModelTest.testSpecificMethod
```

## Architecture

### MVVM Pattern Structure

The app follows **clean architecture with MVVM**:

```
ui/screens/{feature}/
├── {Feature}ViewModel.kt        # @HiltViewModel, manages state
├── {Feature}UiState.kt          # Extends BaseUiState (isLoading, errorMessage)
├── {Feature}Screen.kt           # Composable UI
└── components/                  # Screen-specific components
```

**Key Conventions**:
- All UI states extend `BaseUiState` interface
- State management uses `MutableStateFlow` and `StateFlow`
- `.collectAsStateWithLifecycle()` in Compose to prevent memory leaks
- `.update {}` builder for thread-safe state modifications

### Data Layer (3-Tier Model)

Models exist in three forms with extension functions for conversion:
- **Remote**: API response models (`TmdbMovieRemote`) - in `model/remote/`
- **Domain**: Business logic models (`Movie`) - in `model/local/`
- **Room**: Database entities (`MovieTable`) - in `model/room/`

Conversions: `.toDomain()`, `.toRoom()`, etc.

### Repository Pattern

The repository layer abstracts three datasources:
- **TmdbDatasource**: TMDB API via Retrofit
- **RoomDatasource**: Local SQLite database
- **FirebaseDatabaseDatasource**: Firestore with real-time sync

Located in `/repositories/`, all datasources are injected via Hilt and exposed through the `Repository` interface.

### Firebase + Room Synchronization

The app uses an **offline-first architecture** with **Repository Mediator pattern** and **write-through cache**. Room is the single source of truth for the UI; Firebase syncs in the background.

> **Full documentation**: See the `firebase-specialist` agent (`.claude/agents/firebase-specialist.md`) for architecture diagrams, code patterns, best practices, and troubleshooting.

**Key rules** (always follow these):
- ViewModels observe Room Flows, NEVER Firebase directly
- Write to Firebase first, then immediately to Room (write-through)
- Call `startSync()` in ViewModel init, `stopSync()` in `onCleared()`
- Datasources are decoupled: Firebase and Room have NO dependency on each other
- Repository is the ONLY layer that knows about both datasources

### Dependency Injection

Hilt modules in `/di/`:
- **ApplicationModule**: DispatcherProvider, CoroutineScope, WorkManager
- **NetworkModule**: Retrofit, OkHttpClient, TmdbApi
- **FirebaseModule**: FirebaseAuth, FirebaseFirestore
- **GoogleSignInModule**: Credential Manager for Google Sign-In
- **LocalStoreModule**: SharedPreferences, Room database, DAOs
- **RepositoryModule**: Repository implementations

All modules use `@InstallIn(SingletonComponent::class)` for app-scoped dependencies.

### Navigation

- **Type**: Jetpack Compose Navigation with `NavHost`
- **Location**: `/navigation/`
- **Routes**: Login, Home, Groups, Profile, Details (defined in `Routes.kt`)
- **Type Safety**: Custom `navtypes/` for passing complex objects (e.g., `Movie`)
- **Pattern**: Single Activity architecture - all navigation via Compose

### Shared State

`AuthViewModel` in `/ui/sharedViewmodel/` provides authentication state across the entire app. It's shared by all screens to determine login status.

## Testing

- **Unit Tests**: `/app/src/test/` - ViewModel logic, MockK, Truth, `runTest`, `MainDispatcherRule`
- **Instrumentation Tests**: `/app/src/androidTest/` - Compose UI tests with Hilt test modules (`FakeRepository`, in-memory Room)
- **Test Runner**: `CustomHiltTestRunner`

## Code Style

### Ktlint Configuration

- **Version**: 1.4.1
- **Rules**: Includes `io.nlopez.compose.rules:ktlint` for Compose-specific linting
- **Baseline**: `ktlint-baseline.xml` for suppressing existing violations
- **Auto-formatting**: Run `./gradlew ktlintFormat` before committing

### Conventions

- **Language Features**: Explicit backing fields enabled (`languageSettings.enableLanguageFeature("ExplicitBackingFields")`)
- **Logging**: Use Timber for all logging (`Timber.d()`, `Timber.e()`, etc.)
- **Error Handling**: Custom `CustomException` sealed class for domain errors
- **Async**: Prefer coroutines + Flow over callbacks (except legacy Firebase SDK)

## Firebase Integration

> Use the `firebase-specialist` agent for detailed Firebase architecture, sync patterns, and MCP operations.

- **Auth** (`FirebaseAuthRepository`): Email/password, Google OAuth (Credential Manager), email verification, account deletion
- **Firestore** (`FirebaseDatabaseDatasource`): Real-time sync to Room. Structure: `FFA/{buildType}/users|groups|movies`
- **Crashlytics**: Enabled in release, disabled in debug (`FamilyFilmApp.kt`)
- **MCP**: Firebase MCP server configured for direct Firestore/Auth queries from Claude Code

## Jira Integration

> Use the `jira-specialist` agent (`.claude/agents/jira-specialist.md`) for ticket creation, sprint management, epic organization, and development workflow tracking.

- **Instance**: `apptolast.atlassian.net`
- **Project Key**: `FFA`
- **Board**: FFA board (id: 1)
- **MCP**: Atlassian MCP server configured for direct Jira operations from Claude Code

## GitHub Integration

> Use the `github-specialist` agent (`.claude/agents/github-specialist.md`) for PR management, code reviews, branch operations, and release management.

- **Organization**: `apptolast`
- **Repository**: `apptolast/FamilyFilmApp`
- **Default Branch**: `develop`
- **MCP**: GitHub MCP server configured for direct repo operations from Claude Code

## Development Workflow

> **Jira is the source of truth** for task tracking. The `FFA-XXX` codes come from Jira.

1. **Create Jira ticket** → Auto-generates `FFA-XXX` key
2. **Create branch** from `develop` → `feature/FFA-XXX-short-description`
3. **Implement** → Commit messages reference `FFA-XXX`
4. **Create PR** → Title: `FFA-XXX Description`, target: `develop`
5. **Code review** → Team reviews and approves
6. **Squash and Rebase** to `develop` → Maintains linear history
7. **Cleanup** → Delete branch, update local `develop`

**Branch naming**: `{type}/FFA-XXX-short-description` where type is `feature/`, `fix/`, `refactor/`, `test/`

**Merge strategy**: Always **squash and rebase** to keep `develop` history linear and traceable via `FFA-XXX` codes.

## CI/CD

### GitHub Actions Workflows

**build.yml**: Runs on PRs and pushes to `develop`
- Builds project
- Runs unit tests
- Uploads test reports
- Submits dependency graph

**Secrets Required**:
- `FIREBASE_JSON`: google-services.json content
- `WEB_ID_CLIENT`: Google OAuth client ID
- `TMDB_ACCESS_TOKEN`: TMDB API key
- `ADMOB_APPLICATION_ID`: AdMob app ID
- `ADMOB_BOTTOM_BANNER_ID`: AdMob banner ID

## Adding New Features

### Adding a Screen

1. Create `/ui/screens/{feature}/` directory
2. Create `{Feature}ViewModel` with `@HiltViewModel` annotation
3. Create `{Feature}UiState` data class extending `BaseUiState`
4. Create `{Feature}Screen.kt` composable
5. Add route to `Routes.kt`
6. Update `AppNavigation()` to include the new screen

### Adding API Endpoints

1. Add endpoint to `/network/TmdbApi.kt`
2. Implement in `TmdbDatasourceImpl`
3. Expose through `Repository` interface and `RepositoryImpl`
4. Call from ViewModel via injected repository

### Adding Database Entities

1. Create entity in `/model/room/`
2. Create DAO in `/room/`
3. Add DAO to `AppDatabase.kt`
4. Provide DAO in `LocalStoreModule.kt`
5. Implement datasource access in `RoomDatasourceImpl`

## Important Notes

### Build Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **JVM Target**: 11
- **Kotlin**: 2.2.20

### ProGuard

Release builds use ProGuard with minification and resource shrinking enabled. Rules in `proguard-rules.pro`.

### Room Schema

Room schema directory: `app/schemas/` - version controlled for database migrations.

### Background Work

Background synchronization is handled by the Repository layer with real-time Firebase listeners (see **Firebase + Room Synchronization Architecture**). The app no longer uses WorkManager for sync operations.

For other background tasks (if needed in the future), use WorkManager with Hilt integration via `HiltWorker` annotation.

### Paging

Movie lists use Jetpack Paging 3 with `MoviePagingSource` for efficient data loading from TMDB API.