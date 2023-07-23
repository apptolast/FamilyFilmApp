package com.digitalsolution.utils

sealed class Exceptions(val title: String): Throwable() {
    object ErrorSignInPasswordException: Exceptions("Error with the user")
}