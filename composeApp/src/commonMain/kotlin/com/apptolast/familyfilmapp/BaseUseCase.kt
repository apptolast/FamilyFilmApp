package com.apptolast.familyfilmapp

abstract class BaseUseCase<in P, out R> {

    suspend operator fun invoke(parameters: P): R = execute(parameters)

    protected abstract suspend fun execute(parameters: P): R
}
