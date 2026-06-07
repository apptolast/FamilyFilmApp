package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.analytics.FirebaseAnalyticsTracker
import com.apptolast.familyfilmapp.auth.AppleTokenRevoker
import com.apptolast.familyfilmapp.auth.FirebaseAppleTokenRevoker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.firebase.FirebaseCurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.types.MediaType
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
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseStorageDatasource
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseStorageDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.RecommendedCardStateDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RecommendedCardStateDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasourceImpl
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.buildAppDatabase
import com.apptolast.familyfilmapp.ui.screens.chat.ChatViewModel
import com.apptolast.familyfilmapp.ui.screens.detail.DetailsViewModel
import com.apptolast.familyfilmapp.ui.screens.discover.DiscoverViewModel
import com.apptolast.familyfilmapp.ui.screens.discover.MediaShuffler
import com.apptolast.familyfilmapp.ui.screens.discover.RandomMediaShuffler
import com.apptolast.familyfilmapp.ui.screens.groups.GroupDetailViewModel
import com.apptolast.familyfilmapp.ui.screens.groups.GroupsViewModel
import com.apptolast.familyfilmapp.ui.screens.home.HomeViewModel
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileViewModel
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.utils.DefaultDispatcherProvider
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class

    single { buildTmdbHttpClient(get()) }
    singleOf(::TmdbLocaleManager)
    singleOf(::TmdbApiKtor) bind TmdbApi::class

    single { buildAppDatabase(get()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().groupDao() }
    single { get<AppDatabase>().groupMovieStatusDao() }
    single { get<AppDatabase>().chatMessageDao() }
    single { get<AppDatabase>().skippedMediaDao() }

    singleOf(::FirebaseAuthRepositoryImpl) bind FirebaseAuthRepository::class
    singleOf(::GeminiChatService)
    singleOf(::FirebaseAppleTokenRevoker) bind AppleTokenRevoker::class
    singleOf(::FirebaseAnalyticsTracker) bind AnalyticsTracker::class
    singleOf(::CrashReporter)
    singleOf(::FirebaseCurrentUserIdProvider) bind CurrentUserIdProvider::class
    singleOf(::FirebaseDatabaseDatasourceImpl) bind FirebaseDatabaseDatasource::class
    singleOf(::FirebaseStorageDatasourceImpl) bind FirebaseStorageDatasource::class

    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    singleOf(::TmdbDatasourceImpl) bind TmdbDatasource::class
    singleOf(::RoomDatasourceImpl) bind RoomDatasource::class
    singleOf(::RecommendedCardStateDatasourceImpl) bind RecommendedCardStateDatasource::class
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::RepositoryImpl) bind Repository::class
    singleOf(::RandomMediaShuffler) bind MediaShuffler::class
}

val presentationModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DiscoverViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::GroupsViewModel)
    viewModelOf(::ProfileViewModel)

    viewModel { params ->
        GroupDetailViewModel(
            repository = get(),
            analyticsTracker = get(),
            crashReporter = get(),
            currentUserIdProvider = get(),
            cardStateStore = get(),
            groupId = params.get<String>(),
        )
    }

    // DetailsViewModel takes route payload (mediaId + mediaType) as constructor parameters.
    viewModel { params ->
        DetailsViewModel(
            repository = get(),
            dispatcherProvider = get(),
            analyticsTracker = get(),
            crashReporter = get(),
            currentUserIdProvider = get(),
            mediaId = params.get<Int>(),
            mediaType = params.get<MediaType>(),
        )
    }
}
