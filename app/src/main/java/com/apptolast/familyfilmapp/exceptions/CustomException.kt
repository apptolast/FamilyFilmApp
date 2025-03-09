package com.apptolast.familyfilmapp.exceptions

sealed class CustomException(val error: String? = null) {
    data class GenericException(val value: String? = null) : CustomException(value?.trim())
}
