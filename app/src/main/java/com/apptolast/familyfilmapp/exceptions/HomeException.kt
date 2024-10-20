package com.apptolast.familyfilmapp.exceptions

sealed interface HomeException : CustomException {
    data class MovieException(override val error: String = "Movies not retrieved") : HomeException
    data class GroupsException(override val error: String = "Group not retrieved") : HomeException
}
