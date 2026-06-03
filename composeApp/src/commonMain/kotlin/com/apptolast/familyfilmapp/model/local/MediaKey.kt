package com.apptolast.familyfilmapp.model.local

import com.apptolast.familyfilmapp.model.local.types.MediaType

data class MediaKey(
    val mediaId: Int,
    val mediaType: MediaType,
)

val Media.key: MediaKey
    get() = MediaKey(id, mediaType)
