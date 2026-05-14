package com.apptolast.familyfilmapp.di

import org.koin.dsl.module

// Data layer: repositories, datasources, HTTP client, Room DAOs, Firebase
// wrappers. Populated by blocks 8 (Ktor), 9 (Room), 10 (Firebase) and 11
// (Repository) of the migration plan.
val dataModule = module {
}

// Presentation layer: ViewModels declared via viewModelOf(::ClassName).
// Populated by block 12 of the migration plan.
val presentationModule = module {
}
