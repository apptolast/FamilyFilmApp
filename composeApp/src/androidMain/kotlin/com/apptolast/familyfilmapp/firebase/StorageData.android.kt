package com.apptolast.familyfilmapp.firebase

import dev.gitlive.firebase.storage.Data

actual fun ByteArray.toStorageData(): Data = Data(this)
