package com.apptolast.familyfilmapp.network.interceptors

import com.apptolast.familyfilmapp.BuildConfig
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().newBuilder().apply {
            header("Content-Type", "application/json")
            header("Authorization", "Bearer ${BuildConfig.TMDB_ACCESS_TOKEN}")
        }.also {
            return chain.proceed(it.build())
        }
    }
}
