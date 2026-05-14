package com.apptolast.familyfilmapp.model.local

/**
 * Represents the synchronization state between Firebase and local Room database.
 * This allows the UI to show appropriate feedback to users about data freshness.
 */
sealed interface SyncState {
    /**
     * Data is successfully synced with Firebase.
     * All local data is up-to-date with the remote source.
     */
    data object Synced : SyncState

    /**
     * Currently synchronizing data with Firebase.
     * Background sync operation is in progress.
     */
    data object Syncing : SyncState

    /**
     * Synchronization error occurred.
     * Local data may be stale. User should be notified.
     *
     * @param message Human-readable error message
     * @param throwable Optional exception for debugging
     */
    data class Error(val message: String, val throwable: Throwable? = null) : SyncState

    /**
     * App is offline.
     * Showing cached data from Room database.
     * Changes will be synced when connection is restored.
     */
    data object Offline : SyncState
}
