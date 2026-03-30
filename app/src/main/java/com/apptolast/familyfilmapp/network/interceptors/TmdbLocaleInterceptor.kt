package com.apptolast.familyfilmapp.network.interceptors

import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TmdbLocaleInterceptor @Inject constructor(private val localeManager: TmdbLocaleManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        if (originalUrl.queryParameter("language") != null) {
            val newUrl = originalUrl.newBuilder()
                .setQueryParameter("language", localeManager.languageTag.value)
                .build()

            return chain.proceed(originalRequest.newBuilder().url(newUrl).build())
        }

        return chain.proceed(originalRequest)
    }
}
