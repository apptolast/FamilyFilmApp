package com.digitalsolution.familyfilmapp.state

sealed class CustomResult {
    data class InProgress(val task: Boolean) : CustomResult()
    data class IsError(val task: Boolean) : CustomResult()

    sealed class Complete : CustomResult() {
        data class Success<T>(val file: T) : Complete()
        data class Failed(val error: Throwable) : Complete()
        object Cancelled : Complete()
    }
}
