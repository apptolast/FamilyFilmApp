package com.apptolast.familyfilmapp.exceptions

sealed class HomeException(
    message: String,
) : CustomException(message) {

    data class MovieException(
        val value: String = "Movies not retrieved",
    ) : HomeException(value)
    data class GroupsException(
        val value: String = "Group not retrieved",
    ) : HomeException(value)
}
