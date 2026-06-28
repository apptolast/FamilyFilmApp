package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchResultRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote

/**
 * Offline [TmdbDatasource] that serves entirely fictional titles for App Store
 * screenshots. It never touches the network and never returns real/copyrighted
 * posters — every item carries a [DEMO_POSTER_SCHEME] sentinel posterPath so the
 * poster renderer draws original generated artwork instead of loading an image.
 *
 * Activated only when the app is launched in demo mode (see [com.apptolast.familyfilmapp.di.initKoin]),
 * which in turn is only triggered by the screenshot UI test's launch argument.
 */
class FakeTmdbDatasource : TmdbDatasource {

    override suspend fun getPopularMovies(page: Int): List<TmdbMovieRemote> = if (page > 1) emptyList() else demoMovies

    override suspend fun searchMovieByName(string: String): List<TmdbMovieRemote> = demoMovies

    override suspend fun searchMovieById(movieId: Int): TmdbMovieRemote =
        demoMovies.firstOrNull { it.id == movieId } ?: demoMovies.first()

    override suspend fun getPopularTvShows(page: Int): List<TmdbTvShowRemote> =
        if (page > 1) emptyList() else demoTvShows

    override suspend fun searchMulti(query: String): List<TmdbMultiSearchResultRemote> = demoMulti

    override suspend fun getTvShowById(tvId: Int): TmdbTvShowRemote =
        demoTvShows.firstOrNull { it.id == tvId } ?: demoTvShows.first()

    private companion object {
        // posterPath sentinel: "demo://<seed>" tells the poster renderer to draw
        // original generated art rather than hit the TMDB image CDN.
        const val DEMO_POSTER_SCHEME = "demo://"

        private fun movie(id: Int, title: String, overview: String, voteAverage: Float, releaseDate: String) =
            TmdbMovieRemote(
                id = id,
                adult = false,
                title = title,
                popularity = 100f - id,
                voteAverage = voteAverage,
                overview = overview,
                releaseDate = releaseDate,
                posterPath = "$DEMO_POSTER_SCHEME$id",
            )

        private fun tvShow(
            id: Int,
            name: String,
            overview: String,
            voteAverage: Float,
            firstAirDate: String,
            seasons: Int,
            episodes: Int,
        ) = TmdbTvShowRemote(
            id = id,
            adult = false,
            name = name,
            popularity = 100f - id,
            voteAverage = voteAverage,
            overview = overview,
            firstAirDate = firstAirDate,
            posterPath = "$DEMO_POSTER_SCHEME$id",
            numberOfSeasons = seasons,
            numberOfEpisodes = episodes,
        )

        val demoMovies: List<TmdbMovieRemote> = listOf(
            movie(
                id = 901,
                title = "Stellar Drift",
                overview = "A salvage pilot adrift between dying stars races to deliver the last seed " +
                    "vault before her ship runs out of light.",
                voteAverage = 8.4f,
                releaseDate = "2024-03-15",
            ),
            movie(
                id = 902,
                title = "The Last Lighthouse",
                overview = "On a coast the maps forgot, a keeper guards a flame that holds back more " +
                    "than the dark — and the family that comes looking for him.",
                voteAverage = 7.9f,
                releaseDate = "2023-11-02",
            ),
            movie(
                id = 903,
                title = "Paper Kingdoms",
                overview = "Two rival origami artists fold an entire city into being, only to discover " +
                    "their creations have begun rewriting the rules.",
                voteAverage = 7.2f,
                releaseDate = "2024-07-19",
            ),
            movie(
                id = 904,
                title = "Midnight Cartographer",
                overview = "Every night a tired clerk maps the streets that only appear after midnight, " +
                    "until one of them maps a way back to someone he lost.",
                voteAverage = 8.1f,
                releaseDate = "2022-09-30",
            ),
            movie(
                id = 905,
                title = "Glass Orchard",
                overview = "A botanist nurtures the world's last grove of crystalline trees while a " +
                    "drought threatens to shatter everything she has grown.",
                voteAverage = 6.8f,
                releaseDate = "2023-05-12",
            ),
            movie(
                id = 906,
                title = "Echoes of Tomorrow",
                overview = "A retired courier receives a message from a future that has not happened yet, " +
                    "and must decide which version of it to believe.",
                voteAverage = 7.6f,
                releaseDate = "2024-01-26",
            ),
        )

        val demoTvShows: List<TmdbTvShowRemote> = listOf(
            tvShow(
                id = 951,
                name = "Northern Lanterns",
                overview = "Each winter a remote village lights a thousand lanterns to keep an old " +
                    "promise — and a new arrival learns why no one ever leaves before spring.",
                voteAverage = 8.7f,
                firstAirDate = "2023-10-04",
                seasons = 2,
                episodes = 16,
            ),
            tvShow(
                id = 952,
                name = "The Quiet Frequency",
                overview = "A late-night radio host and her listeners untangle a mystery broadcast on a " +
                    "channel that should not exist.",
                voteAverage = 8.0f,
                firstAirDate = "2024-02-14",
                seasons = 1,
                episodes = 8,
            ),
            tvShow(
                id = 953,
                name = "Harbor & Hollow",
                overview = "Two siblings inherit a crumbling seaside inn and the tangle of secrets the " +
                    "town has been keeping under its floorboards.",
                voteAverage = 7.5f,
                firstAirDate = "2022-08-21",
                seasons = 3,
                episodes = 30,
            ),
            tvShow(
                id = 954,
                name = "Clockwork Meadow",
                overview = "In a valley where the seasons run on gears, a young engineer fights to keep " +
                    "spring from rusting away forever.",
                voteAverage = 7.8f,
                firstAirDate = "2024-06-09",
                seasons = 1,
                episodes = 10,
            ),
            tvShow(
                id = 955,
                name = "Saffron Streets",
                overview = "A travelling cook and a stranded musician turn one chaotic night market into " +
                    "the heart of a neighbourhood finding itself again.",
                voteAverage = 8.2f,
                firstAirDate = "2023-04-18",
                seasons = 2,
                episodes = 20,
            ),
            tvShow(
                id = 956,
                name = "The Velvet Atlas",
                overview = "A bookbinder discovers that the maps she mends quietly redraw the world they " +
                    "describe, and someone is trying to tear a page out.",
                voteAverage = 7.4f,
                firstAirDate = "2024-09-01",
                seasons = 1,
                episodes = 6,
            ),
        )

        // Multi-search blends movies and TV so the search surfaces stay on demo data too.
        val demoMulti: List<TmdbMultiSearchResultRemote> = buildList {
            demoMovies.forEach { movie ->
                add(
                    TmdbMultiSearchResultRemote(
                        id = movie.id,
                        mediaType = "movie",
                        adult = false,
                        title = movie.title,
                        popularity = movie.popularity,
                        voteAverage = movie.voteAverage,
                        overview = movie.overview,
                        releaseDate = movie.releaseDate,
                        posterPath = movie.posterPath,
                    ),
                )
            }
            demoTvShows.forEach { show ->
                add(
                    TmdbMultiSearchResultRemote(
                        id = show.id,
                        mediaType = "tv",
                        adult = false,
                        name = show.name,
                        popularity = show.popularity,
                        voteAverage = show.voteAverage,
                        overview = show.overview,
                        firstAirDate = show.firstAirDate,
                        posterPath = show.posterPath,
                    ),
                )
            }
        }
    }
}
