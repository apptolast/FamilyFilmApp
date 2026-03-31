package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val adult: Boolean,
    val popularity: Float,
    val voteAverage: Float,
    val streamProviders: List<Provider>,
    val buyProviders: List<Provider>,
    val rentProviders: List<Provider>,
    val releaseDate: String,
    val overview: String,
    val posterPath: String,
) : Parcelable {

    constructor() : this(title = "", posterPath = "")

    constructor(
        title: String,
        posterPath: String,
    ) : this(
        id = 0,
        title = title,
        adult = false,
        popularity = 0f,
        voteAverage = 0f,
        streamProviders = emptyList(),
        buyProviders = emptyList(),
        rentProviders = emptyList(),
        releaseDate = "",
        overview = "",
        posterPath = posterPath,
    )
}

fun TmdbMovieRemote.toDomain(countryCode: String): Movie = Movie(
    id = id,
    adult = adult,
    title = title ?: "",
    popularity = popularity ?: 0f,
    voteAverage = voteAverage ?: 0f,
    streamProviders = providers?.results?.get(countryCode)?.stream?.map { provider ->
        Provider(
            providerId = provider.providerId,
            name = provider.providerName,
            logoPath = provider.logoPath,
        )
    } ?: emptyList(),
    buyProviders = providers?.results?.get(countryCode)?.buy?.map { provider ->
        Provider(
            providerId = provider.providerId,
            name = provider.providerName,
            logoPath = provider.logoPath,
        )
    } ?: emptyList(),
    rentProviders = providers?.results?.get(countryCode)?.rent?.map { provider ->
        Provider(
            providerId = provider.providerId,
            name = provider.providerName,
            logoPath = provider.logoPath,
        )
    } ?: emptyList(),
    overview = overview ?: "",
    releaseDate = releaseDate ?: "",
    posterPath = posterPath ?: "",
)
