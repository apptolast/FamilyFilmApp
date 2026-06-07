package com.apptolast.familyfilmapp.firebase

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create

// Uses Kotlin/Native's built-in Foundation bindings — no custom cinterop .def.
@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.toStorageData(): Data = Data(toNSData())

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = if (isEmpty()) {
    NSData()
} else {
    usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}
