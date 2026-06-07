package com.apptolast.familyfilmapp.testing

import com.apptolast.familyfilmapp.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(private val dispatcher: CoroutineDispatcher) : DispatcherProvider {
    override fun main(): CoroutineDispatcher = dispatcher
    override fun default(): CoroutineDispatcher = dispatcher
    override fun io(): CoroutineDispatcher = dispatcher
    override fun unconfined(): CoroutineDispatcher = dispatcher
}
