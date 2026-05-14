package com.apptolast.familyfilmapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// Single entry point that starts Koin with the three project modules.
// - Android calls it from FamilyFilmApp.onCreate() with androidContext(this).
// - iOS calls it from MainViewController via initKoinForIos() below.
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, presentationModule)
    }
}

// Bridge for the Swift side. Called from iOSApp.swift (block 5)
// before the SwiftUI tree mounts ContentView.
@Suppress("unused")
fun initKoinForIos() {
    initKoin()
}
