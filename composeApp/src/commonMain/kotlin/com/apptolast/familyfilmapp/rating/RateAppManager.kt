package com.apptolast.familyfilmapp.rating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface RateAppManager {
    val hasRatedApp: StateFlow<Boolean>
    fun markAsRated()
}

class NoOpRateAppManager : RateAppManager {
    private val _hasRated = MutableStateFlow(false)
    override val hasRatedApp: StateFlow<Boolean> = _hasRated.asStateFlow()
    override fun markAsRated() {
        _hasRated.value = true
    }
}
