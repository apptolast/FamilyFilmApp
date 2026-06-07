package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.firebase.toStorageData
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage

interface FirebaseStorageDatasource {
    /** Uploads the group's avatar and returns its download URL. */
    suspend fun uploadGroupImage(groupId: String, bytes: ByteArray): String
}

// Layout mirrors Firestore: FFA/{BUILD_TYPE}/groupImages/{groupId}.jpg
class FirebaseStorageDatasourceImpl : FirebaseStorageDatasource {

    override suspend fun uploadGroupImage(groupId: String, bytes: ByteArray): String {
        val ref = Firebase.storage.reference
            .child("$DB_ROOT/${BuildConfig.BUILD_TYPE}/groupImages/$groupId.jpg")
        ref.putData(bytes.toStorageData())
        return ref.getDownloadUrl()
    }

    private companion object {
        const val DB_ROOT = "FFA"
    }
}
