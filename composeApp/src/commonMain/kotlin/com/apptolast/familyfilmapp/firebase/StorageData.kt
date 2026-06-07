package com.apptolast.familyfilmapp.firebase

import dev.gitlive.firebase.storage.Data

/**
 * Wraps raw bytes into GitLive's platform-specific [Data] for Storage uploads
 * (Android backs it with a ByteArray, iOS with NSData).
 */
expect fun ByteArray.toStorageData(): Data
