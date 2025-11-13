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

### Firebase + Room Synchronization Architecture

The app implements a **professional offline-first architecture** using the **Repository Mediator pattern** with **write-through cache**. This ensures data consistency, offline support, and real-time synchronization.

#### Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│              ViewModel Layer                     │
│  • Calls startSync() on init                    │
│  • Calls stopSync() on onCleared()              │
│  • Observes Room (single source of truth)       │
│  • Observes SyncState for UI feedback           │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│           Repository (Mediator)                  │
│  • Orchestrates Firebase ↔ Room sync            │
│  • startSync(): Firebase listener → Room write  │
│  • Write-through: Firebase write + Room update  │
│  • Exposes SyncState Flow for observability     │
│  • Single CoroutineScope for lifecycle          │
└──────────┬──────────────────────┬────────────────┘
           │                      │
           ▼                      ▼
┌──────────────────┐    ┌──────────────────┐
│    Firebase      │    │      Room        │
│  (Remote)        │    │   (Local Cache)  │
│  • Real-time     │    │  • Single source │
│    listeners     │    │    of truth      │
│  • Offline       │    │  • Flow queries  │
│    persistence   │    │  • Fast access   │
└──────────────────┘    └──────────────────┘
```

#### Core Principles

1. **Single Source of Truth**: Room is the ONLY source of truth for the UI
   - ViewModels NEVER observe Firebase directly
   - All UI data comes from Room Flows
   - Ensures consistent state across the app

2. **Repository as Mediator**: Repository orchestrates synchronization
   - Manages Firebase listeners lifecycle
   - Writes Firebase data to Room
   - Handles bidirectional sync
   - Maintains SyncState

3. **Write-Through Pattern**: Writes update both stores immediately
   - Write to Firebase first (source of truth for backend)
   - Immediately write to Room (instant UI update)
   - No waiting for listener to propagate changes

4. **Layer Independence**: Datasources are decoupled
   - `FirebaseDatabaseDatasource` has NO dependency on `RoomDatasource`
   - `RoomDatasource` has NO knowledge of Firebase
   - Repository is the only layer that knows about both

#### Key Components

##### 1. SyncState (model/local/SyncState.kt)

Type-safe representation of synchronization state:

```kotlin
sealed interface SyncState {
    data object Synced : SyncState          // All data synchronized
    data object Syncing : SyncState         // Currently syncing
    data class Error(                       // Sync error occurred
        val message: String,
        val throwable: Throwable? = null
    ) : SyncState
    data object Offline : SyncState         // No network connection
}
```

**Usage in ViewModel:**
```kotlin
private fun observeSyncState() {
    viewModelScope.launch {
        repository.getSyncState().collectLatest { syncState ->
            _state.update { it.copy(syncState = syncState) }
        }
    }
}
```

##### 2. Repository Sync Lifecycle

**Starting Sync (init block):**
```kotlin
init {
    currentUserId = auth.currentUser?.uid
    if (currentUserId != null) {
        repository.startSync(currentUserId!!)
        observeSyncState()
    }
}
```

**Stopping Sync (cleanup):**
```kotlin
override fun onCleared() {
    repository.stopSync()
    super.onCleared()
}
```

##### 3. Repository Implementation (repositories/Repository.kt)

**Sync Management:**
```kotlin
private val _syncState = MutableStateFlow<SyncState>(SyncState.Synced)
private var syncJob: Job? = null

override fun startSync(userId: String) {
    syncJob?.cancel()
    syncJob = coroutineScope.launch {
        try {
            _syncState.value = SyncState.Syncing
            firebaseDatabaseDatasource.getMyGroups(userId)
                .collect { remoteGroups ->
                    remoteGroups.forEach { group ->
                        roomDatasource.insertGroup(group.toGroupTable())
                    }
                    _syncState.value = SyncState.Synced
                }
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(
                message = e.message ?: "Unknown error",
                throwable = e
            )
        }
    }
}

override fun stopSync() {
    syncJob?.cancel()
    syncJob = null
    _syncState.value = SyncState.Synced
}
```

**Write-Through Pattern:**
```kotlin
override suspend fun createGroup(
    groupName: String,
    userId: String
): Result<Group> = suspendCancellableCoroutine { continuation ->
    firebaseDatabaseDatasource.createGroup(
        groupName = groupName,
        user = user,
        success = { group ->
            // Write-through: immediately update Room
            coroutineScope.launch {
                try {
                    roomDatasource.insertGroup(group.toGroupTable())
                    Timber.d("Group created and synced to Room")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing to Room")
                }
            }
            continuation.resume(Result.success(group))
        },
        failure = { error ->
            continuation.resume(Result.failure(error))
        }
    )
}
```

##### 4. Firebase Offline Persistence (di/FirebaseModule.kt)

Enable Firestore offline caching:

```kotlin
@Provides
@Singleton
fun provideFirebaseFirestore(): FirebaseFirestore =
    Firebase.firestore.also { firestore ->
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        firestore.firestoreSettings = settings
        Timber.d("Firebase offline persistence enabled")
    }
```

##### 5. UI Sync Indicators (ui/screens/.../Screen.kt)

Display sync state to users:

```kotlin
@Composable
fun SyncStateIndicator(syncState: SyncState) {
    AnimatedVisibility(visible = syncState !is SyncState.Synced) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (syncState) {
                is SyncState.Syncing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.width(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sync_state_syncing),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is SyncState.Error -> {
                    Text(
                        text = "Sync error: ${syncState.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is SyncState.Offline -> {
                    Text(
                        text = stringResource(R.string.sync_state_offline),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }
        }
    }
}
```

#### Data Flow Examples

**Read Flow (ViewModel observes Room):**
```
1. User opens screen
2. ViewModel observes repository.getMyGroups(userId)
3. Repository returns Room Flow<List<Group>>
4. UI displays data from Room
5. Background: Firebase listener updates Room
6. Room Flow emits new data
7. UI automatically updates
```

**Write Flow (Write-through pattern):**
```
1. User creates group
2. ViewModel calls repository.createGroup()
3. Repository writes to Firebase
4. Repository immediately writes to Room (write-through)
5. UI updates instantly from Room
6. Firebase listener eventually triggers (idempotent)
7. Room ignores duplicate (same data)
```

#### Best Practices

**DO:**
- ✅ Always call `startSync()` in ViewModel init when user is authenticated
- ✅ Always call `stopSync()` in ViewModel `onCleared()`
- ✅ Observe Room Flows in ViewModels, never Firebase directly
- ✅ Use write-through pattern for all mutations (Firebase + Room)
- ✅ Handle SyncState in UI for user feedback
- ✅ Use `suspendCancellableCoroutine` for callback-to-suspend conversion
- ✅ Log sync operations with Timber for debugging
- ✅ Use `Result<T>` return types for Repository suspend functions

**DON'T:**
- ❌ Never inject RoomDatasource into FirebaseDatabaseDatasource
- ❌ Never inject FirebaseDatabaseDatasource into RoomDatasource
- ❌ Never observe Firebase directly from ViewModels
- ❌ Never write to Room without writing to Firebase first (on mutations)
- ❌ Never forget to cancel sync on ViewModel cleanup
- ❌ Never use callbacks in Repository interface (use suspend functions)
- ❌ Never start listeners in datasources (Repository manages lifecycle)

#### Common Patterns

**Suspend Function with Write-Through:**
```kotlin
override suspend fun updateGroup(group: Group): Result<Unit> =
    suspendCancellableCoroutine { continuation ->
        firebaseDatabaseDatasource.updateGroup(
            group = group,
            success = {
                // Write-through to Room
                coroutineScope.launch {
                    roomDatasource.insertGroup(group.toGroupTable())
                }
                continuation.resume(Result.success(Unit))
            },
            failure = { continuation.resume(Result.failure(it)) }
        )
    }
```

**ViewModel State with SyncState:**
```kotlin
data class ScreenState(
    val data: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val syncState: SyncState = SyncState.Synced  // Add this
)
```

**Collecting Room Flow in ViewModel:**
```kotlin
private fun observeData() {
    viewModelScope.launch {
        repository.getData(userId).collectLatest { items ->
            _state.update { it.copy(data = items, isLoading = false) }
        }
    }
}
```

#### Troubleshooting

**Issue: Data not syncing**
- Check if `startSync()` is called in ViewModel init
- Verify Firebase listener is active (check Timber logs)
- Ensure user is authenticated (`auth.currentUser != null`)
- Check network connectivity

**Issue: Duplicate writes**
- This is expected and safe (write-through + listener)
- Room `insertGroup` is idempotent (replaces on conflict)
- No performance impact for small datasets

**Issue: Memory leaks**
- Verify `stopSync()` is called in `onCleared()`
- Check that syncJob is cancelled
- Use `.collectAsStateWithLifecycle()` in Compose

**Issue: UI not updating**
- Ensure ViewModel observes Room, not Firebase
- Check that Room DAO returns Flow, not suspend function
- Verify `.update {}` is used for state modifications

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

### Test Structure

- **Unit Tests**: `/app/src/test/` - ViewModel logic, uses MockK
- **Instrumentation Tests**: `/app/src/androidTest/` - UI tests with Compose, uses Hilt test modules
- **Test Runner**: `CustomHiltTestRunner` for Hilt-powered instrumentation tests

### Test Modules

Instrumentation tests use test DI modules in `/app/src/androidTest/java/com/apptolast/familyfilmapp/di/`:
- `TestApplicationModule` - Replaces production dispatchers
- `TestLocalStoreModule` - In-memory Room database
- `TestRepositoryModule` - Injects `FakeRepository`

### Key Testing Tools

- **MockK**: Mocking framework
- **Truth**: Fluent assertions
- **Coroutines Test**: `runTest` for coroutine testing
- **Compose Test**: `createComposeRule()` for UI testing
- **ArchCoreTesting**: `InstantTaskExecutorRule` for LiveData

### MainDispatcherRule

Unit tests use `MainDispatcherRule` (in `/app/src/test/`) to replace the main dispatcher with a test dispatcher.

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

### Authentication

Handled by `FirebaseAuthRepository`:
- Email/password login and registration
- Google OAuth via Credential Manager
- Email verification flow
- Account deletion with re-authentication

### Firestore

User and group data stored in Firestore with real-time sync to Room database. Accessed via `FirebaseDatabaseDatasource`.

### Crashlytics

Enabled in release builds, disabled in debug. Configured in `FamilyFilmApp.kt`.

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