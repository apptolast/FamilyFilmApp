package com.apptolast.familyfilmapp.exceptions

data class GenericException(override val error: String = "Generic Exception") : CustomException
