package com.apptolast.familyfilmapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, presentationModule)
    }
}

// Bridge for the Swift side.
@Suppress("unused")
fun initKoinForIos() {
    initKoin()
}
