package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Provider(val providerId: Int, val name: String, val logoPath: String) : Parcelable
