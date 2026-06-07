package com.apptolast.familyfilmapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

// Idempotent — second invocation is a no-op so the iOS side can call it
// from MainViewController without worrying about double-init crashes.
fun initKoin(config: KoinAppDeclaration? = null) {
    try {
        KoinPlatform.getKoin()
        return // Already started.
    } catch (_: IllegalStateException) {
        // Not started yet — proceed below.
    }
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, presentationModule)
    }
}
