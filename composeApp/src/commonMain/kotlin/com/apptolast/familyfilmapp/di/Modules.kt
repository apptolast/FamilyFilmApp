package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.ads.NoOpNativeAdManager
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.analytics.FirebaseAnalyticsTracker
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
import com.apptolast.familyfilmapp.auth.NoOpGoogleSignInClient
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.purchases.NoOpPurchaseManager
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.rating.NoOpRateAppManager
import com.apptolast.familyfilmapp.rating.RateAppManager
import com.apptolast.familyfilmapp.ui.screens.chat.ChatViewModel
import com.apptolast.familyfilmapp.ui.screens.detail.DetailsViewModel
import com.apptolast.familyfilmapp.ui.screens.discover.DiscoverViewModel
import com.apptolast.familyfilmapp.ui.screens.groups.GroupViewModel
import com.apptolast.familyfilmapp.ui.screens.home.HomeViewModel
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileViewModel
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import org.koin.core.module.dsl.viewModel
import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.network.TmdbApiKtor
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.network.buildTmdbHttpClient
import com.apptolast.familyfilmapp.repositories.ChatRepository
import com.apptolast.familyfilmapp.repositories.ChatRepositoryImpl
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepositoryImpl
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.repositories.RepositoryImpl
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.buildAppDatabase
import com.apptolast.familyfilmapp.utils.DefaultDispatcherProvider
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

// Data layer: repositories, datasources, HTTP client, Room DAOs, Firebase
// wrappers. Block 8 introduces the Ktor/TMDB graph below; blocks 9, 10 and
// 11 add Room, GitLive Firebase and the repository implementations.
val dataModule = module {
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class

    // TMDB network stack: engine comes from platformModule, the HttpClient
    // wraps it with the bearer token + content negotiation, TmdbApiKtor
    // exposes the typed surface, TmdbLocaleManager stores the user-selected
    // language/region via multiplatform-settings.
    single { buildTmdbHttpClient(get()) }
    singleOf(::TmdbLocaleManager)
    singleOf(::TmdbApiKtor) bind TmdbApi::class

    // Room: platformModule provides the platform-specific RoomDatabase.Builder.
    // buildAppDatabase applies migrations + driver + coroutine context.
    single { buildAppDatabase(get()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().groupDao() }
    single { get<AppDatabase>().groupMovieStatusDao() }
    single { get<AppDatabase>().chatMessageDao() }

    // Firebase facing the rest of the app via small, plain commonMain types
    // backed by GitLive (Auth/Functions/Analytics/Crashlytics — all KMP-safe).
    // App Check provider installation is platform-specific and lives in
    // FamilyFilmApp.onCreate (Android) / iOSApp.swift init (iOS).
    singleOf(::FirebaseAuthRepositoryImpl) bind FirebaseAuthRepository::class
    singleOf(::GeminiChatService)
    singleOf(::FirebaseAnalyticsTracker) bind AnalyticsTracker::class
    singleOf(::CrashReporter)
    singleOf(::FirebaseDatabaseDatasourceImpl) bind FirebaseDatabaseDatasource::class

    // Datasources + repositories (block 11). Repository owns the
    // SyncState-driven Firestore→Room mirroring kicked off from ViewModels
    // via startSync(userId).
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    singleOf(::TmdbDatasourceImpl) bind TmdbDatasource::class
    singleOf(::RoomDatasourceImpl) bind RoomDatasource::class
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::RepositoryImpl) bind Repository::class

    // Platform-specific clients with no-op defaults in commonMain. Blocks 14
    // (Android) and 15 (iOS) override these in their platformModule using
    // `single(..., createdAtStart = true) { override = true }` once the
    // real RevenueCat / Credential Manager / GoogleSignIn-iOS hooks land.
    singleOf(::NoOpPurchaseManager) bind PurchaseManager::class
    singleOf(::NoOpGoogleSignInClient) bind GoogleSignInClient::class
    singleOf(::NoOpNativeAdManager) bind NativeAdManager::class
    singleOf(::NoOpRateAppManager) bind RateAppManager::class
}

/**
 * Presentation layer: ViewModels declared via [viewModelOf]. Block 12b
 * registers the shared [AuthViewModel] first; block 12c adds the seven
 * per-screen ViewModels.
 */
val presentationModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DiscoverViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::GroupViewModel)
    viewModelOf(::ProfileViewModel)

    // DetailsViewModel takes the route payload (mediaId + mediaType) as
    // constructor parameters. Screens resolve it via:
    //   koinViewModel<DetailsViewModel>(parameters = {
    //       parametersOf(details.mediaId, details.mediaType)
    //   })
    viewModel { params ->
        DetailsViewModel(
            repository = get(),
            dispatcherProvider = get(),
            analyticsTracker = get(),
            crashReporter = get(),
            mediaId = params.get<Int>(),
            mediaType = params.get<MediaType>(),
        )
    }
}
