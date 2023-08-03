package com.digitalsolution.familyfilmapp.ui.screens.login

abstract class UseCase<in P, out R> {

    suspend operator fun invoke(parameters: P): R = execute(parameters)

    protected abstract suspend fun execute(parameters: P): R
}