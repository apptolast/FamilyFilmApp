package com.apptolast.familyfilmapp.network.interceptors

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
//        chain.request().newBuilder().apply {
//            header("Content-Type", "application/json")
//            localRepository.getToken().let { token ->
//                if (!token.isNullOrBlank()) {
//                    header("Authorization", "Bearer $token")
//                }
//            }
//        }.also {
        return chain.proceed(chain.request())
//        }
    }
}
