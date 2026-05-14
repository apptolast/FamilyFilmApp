package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.network.TmdbApiKtor
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.network.buildTmdbHttpClient
import com.apptolast.familyfilmapp.utils.DefaultDispatcherProvider
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
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
}

// Presentation layer: ViewModels declared via viewModelOf(::ClassName).
// Populated by block 12 of the migration plan.
val presentationModule = module {
}
