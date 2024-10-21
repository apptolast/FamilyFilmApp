package com.apptolast.familyfilmapp.network.interceptors

import com.apptolast.familyfilmapp.repositories.LocalRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val localRepository: LocalRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().newBuilder().apply {
            header("Content-Type", "application/json")
            localRepository.getToken().let { token ->
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }
        }.also {
            return chain.proceed(it.build())
        }
    }
}
