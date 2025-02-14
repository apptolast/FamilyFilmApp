package com.apptolast.familyfilmapp.extensions

import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.remote.firebase.GroupFirebase
import java.util.Calendar

fun Group.updateModificationDate(): Group = this.copy(
    lastUpdated = Calendar.getInstance().time,
)
