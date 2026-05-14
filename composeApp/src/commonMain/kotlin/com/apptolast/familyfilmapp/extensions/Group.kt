package com.apptolast.familyfilmapp.extensions

import com.apptolast.familyfilmapp.model.local.Group
import kotlinx.datetime.Clock

fun Group.updateModificationDate(): Group = this.copy(
    lastUpdated = Clock.System.now(),
)
