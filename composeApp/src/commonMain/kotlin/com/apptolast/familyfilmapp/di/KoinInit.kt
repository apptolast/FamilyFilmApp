package com.apptolast.familyfilmapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

// Idempotent — second invocation is a no-op so the iOS side can call it
// from MainViewController without worrying about double-init crashes.
//
// `demoMode` swaps the real TMDB datasource for [demoModule]'s fake one so
// App Store screenshots show fictional titles with original generated artwork.
// It defaults to false; production never enables it. Only the screenshot UI
// test launches the app with the demo launch argument (see MainViewController.ios).
fun initKoin(demoMode: Boolean = false, config: KoinAppDeclaration? = null) {
    try {
        KoinPlatform.getKoin()
        return // Already started.
    } catch (_: IllegalStateException) {
        // Not started yet — proceed below.
    }
    startKoin {
        config?.invoke(this)
        // demoModule is appended last so its override binding wins over dataModule.
        val modules = buildList {
            add(platformModule)
            add(dataModule)
            add(presentationModule)
            if (demoMode) add(demoModule)
        }
        modules(modules)
    }
}
