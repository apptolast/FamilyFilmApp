package com.apptolast.familyfilmapp.model.local

sealed interface SyncState {
    data object Synced : SyncState
    data object Syncing : SyncState
    data class Error(val message: String, val throwable: Throwable? = null) : SyncState
    data object Offline : SyncState
}
