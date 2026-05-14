package com.apptolast.familyfilmapp.model.local

import kotlinx.serialization.Serializable

@Serializable
data class Provider(val providerId: Int, val name: String, val logoPath: String)
