package com.apptolast.familyfilmapp.model.local

import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType

data class GroupMediaStatus(
    val groupId: String,
    val userId: String,
    val mediaId: Int,
    val status: MediaStatus,
    val mediaType: MediaType = MediaType.MOVIE,
)
