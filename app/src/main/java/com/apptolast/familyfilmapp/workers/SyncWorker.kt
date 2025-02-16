package com.apptolast.familyfilmapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.apptolast.familyfilmapp.model.room.toGroupTable
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * This Worker is not used in the code anymore as the firestore database listeners for changes are implemented
 * in the FirebaseDatabaseDatasourceImpl class. There is no need to get the room sync in a worker but this class
 * is kept for reference on how to implement a worker with Hilt.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val roomDatasource: RoomDatasource,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: return Result.failure()
            val localGroups = roomDatasource.getGroups().first() // Get all local groups

            // Get groups from Firebase. If they don't exist locally, insert them.
            val firebaseGroupsFlow =
                FirebaseDatabaseDatasourceImpl(
                    database = firestore,
                    roomDatasource = roomDatasource,
                    coroutineScope = CoroutineScope(Dispatchers.IO),
                ).getMyGroups(userId)

            firebaseGroupsFlow.collect { firebaseGroups ->
                firebaseGroups.forEach { firebaseGroup ->
                    val groupTable = firebaseGroup.toGroupTable()
                    roomDatasource.insertGroup(groupTable)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing data to Firebase")
            return Result.retry()
        }
    }
}
