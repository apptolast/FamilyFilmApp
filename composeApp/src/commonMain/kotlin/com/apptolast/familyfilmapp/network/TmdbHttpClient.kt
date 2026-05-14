package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Builds the shared HttpClient for TMDB calls. Replaces the legacy
 * OkHttp + Retrofit setup (AuthInterceptor + TmdbLocaleInterceptor +
 * HttpLoggingInterceptor).
 *
 * - Engine is provided per platform (OkHttp on Android, Darwin on iOS).
 * - Base URL is fixed to TmdbConfig.BASE_URL via defaultRequest.
 * - Authorization header is the static TMDB bearer token from BuildKonfig.
 * - JSON parser is lenient and tolerates unknown TMDB fields.
 */
fun buildTmdbHttpClient(engine: HttpClientEngine): HttpClient {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
    return HttpClient(engine) {
        install(ContentNegotiation) { json(json) }
        // LogLevel.NONE for now to avoid leaking the Authorization header in
        // device logs. A dedicated DEBUG flag in BuildKonfig will gate verbose
        // logging in a later iteration.
        install(Logging) { level = LogLevel.NONE }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.themoviedb.org"
                path("3/")
            }
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_ACCESS_TOKEN}")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }
}
