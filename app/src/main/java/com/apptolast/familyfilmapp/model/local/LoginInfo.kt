package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginInfo(val accessToken: String, val tokenType: String) : Parcelable {
    constructor() : this("", "")
}
