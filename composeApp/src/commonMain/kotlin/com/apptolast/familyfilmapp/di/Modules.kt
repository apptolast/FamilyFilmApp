package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.analytics.FirebaseAnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.network.TmdbApiKtor
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.network.buildTmdbHttpClient
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepositoryImpl
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasourceImpl
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.buildAppDatabase
import com.apptolast.familyfilmapp.utils.DefaultDispatcherProvider
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import org.koin.core.module.dsl.singleOf
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
}

// Presentation layer: ViewModels declared via viewModelOf(::ClassName).
// Populated by block 12 of the migration plan.
val presentationModule = module {
}
