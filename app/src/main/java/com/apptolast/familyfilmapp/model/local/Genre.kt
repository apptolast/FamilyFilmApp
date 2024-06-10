package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(val id: Int, val name: String) : Parcelable {
    constructor() : this(
        id = -1,
        name = "",
    )
}
