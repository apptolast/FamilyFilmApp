package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.network.BackendApi
import com.apptolast.familyfilmapp.network.interceptors.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideBaseUrl(): String = when (BuildConfig.BUILD_TYPE) {
        "debug" -> "https://familyfilmappback-refactor-mio.onrender.com/"
        "staging" -> "https://familyfilmappback-refactor-mio.onrender.com/"
        "release" -> "https://familyfilmappback-refactor-mio.onrender.com/"
        else -> ""
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideScalarsConverterFactory(): ScalarsConverterFactory = ScalarsConverterFactory.create()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder().apply {
            addInterceptor(authInterceptor)
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    },
                )
            }
        }.build()

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        gsonConverterFactory: GsonConverterFactory,
        scalarsConverterFactory: ScalarsConverterFactory,
        client: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(scalarsConverterFactory)
        .addConverterFactory(gsonConverterFactory)
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideServiceApi(retrofit: Retrofit): BackendApi = retrofit.create(BackendApi::class.java)
}
