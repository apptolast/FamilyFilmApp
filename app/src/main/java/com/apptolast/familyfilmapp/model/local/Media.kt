package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchResultRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
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
    val mediaType: MediaType = MediaType.MOVIE,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
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

fun TmdbMovieRemote.toDomain(countryCode: String): Media = Media(
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
    mediaType = MediaType.MOVIE,
)

fun TmdbTvShowRemote.toDomain(countryCode: String): Media = Media(
    id = id,
    adult = adult,
    title = name ?: "",
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
    releaseDate = firstAirDate ?: "",
    posterPath = posterPath ?: "",
    mediaType = MediaType.TV_SHOW,
    numberOfSeasons = numberOfSeasons,
    numberOfEpisodes = numberOfEpisodes,
)

fun TmdbMultiSearchResultRemote.toDomain(countryCode: String): Media? {
    if (mediaType != "movie" && mediaType != "tv") return null
    val type = MediaType.fromTmdbString(mediaType)
    return Media(
        id = id,
        adult = adult,
        title = if (type == MediaType.TV_SHOW) name ?: "" else title ?: "",
        popularity = popularity ?: 0f,
        voteAverage = voteAverage ?: 0f,
        streamProviders = emptyList(),
        buyProviders = emptyList(),
        rentProviders = emptyList(),
        overview = overview ?: "",
        releaseDate = if (type == MediaType.TV_SHOW) firstAirDate ?: "" else releaseDate ?: "",
        posterPath = posterPath ?: "",
        mediaType = type,
    )
}
