package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.network.TmdbConfig
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Sentinel scheme carried by [Media.posterPath] when the app runs in demo mode.
 * The fake datasource emits "demo://<seed>" so this renderer knows to draw an
 * original generated poster instead of loading a real (copyrighted) image or
 * hitting the TMDB image CDN. Production posterPaths never start with this.
 */
const val DEMO_POSTER_SCHEME = "demo://"

private fun String.isDemoPoster(): Boolean = startsWith(DEMO_POSTER_SCHEME)

/** Resolves a [Media.posterPath] to the TMDB CDN URL, mirroring legacy call-site logic. */
private fun posterModel(posterPath: String, sizePath: String): String = when {
    posterPath.isEmpty() -> TmdbConfig.PLACEHOLDER_URL
    else -> "$sizePath$posterPath"
}

/**
 * Single source of truth for rendering a media poster.
 *
 * For normal posterPaths it behaves exactly like the previous inline [AsyncImage]
 * usages (placeholder for empty paths, TMDB CDN otherwise). For demo-mode
 * posterPaths it draws an original Compose poster — no bundled assets, no network —
 * so App Store screenshots show fictional artwork that infringes nothing.
 *
 * @param sizePath TMDB CDN size segment (e.g. [TmdbConfig.POSTER_GRID]). Ignored for demo posters.
 */
@Composable
fun PosterImage(
    media: Media,
    modifier: Modifier = Modifier,
    sizePath: String = TmdbConfig.POSTER_GRID,
    contentScale: ContentScale = ContentScale.Crop,
) {
    if (media.posterPath.isDemoPoster()) {
        GeneratedPoster(media = media, modifier = modifier)
    } else {
        AsyncImage(
            model = posterModel(media.posterPath, sizePath),
            contentDescription = media.title,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}

/**
 * Original, fully Compose-drawn poster: a deterministic gradient derived from the
 * title, the title itself, a fake metascore badge and a genre chip. Tasteful enough
 * to pass as a movie poster in a store screenshot while owning nothing copyrighted.
 */
@Composable
private fun GeneratedPoster(media: Media, modifier: Modifier = Modifier) {
    val palette = posterPalette(media.title)
    val genre = if (media.mediaType == MediaType.TV_SHOW) "SERIES" else "FEATURE"
    val score = (media.voteAverage * 10f).roundToInt().coerceIn(0, 100)

    Box(
        modifier = modifier
            .background(Brush.linearGradient(palette))
            .padding(14.dp),
    ) {
        // Score badge top-start.
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = score.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
        }

        // Genre chip top-end.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.30f))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                text = genre,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
            )
        }

        // Decorative arc/initials in the middle keeps empty posters from looking flat.
        Text(
            text = media.title.firstOrNull()?.uppercase() ?: "F",
            color = Color.White.copy(alpha = 0.12f),
            fontWeight = FontWeight.Black,
            fontSize = 96.sp,
            modifier = Modifier.align(Alignment.Center),
        )

        // Title block bottom.
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 3.dp)
                        .background(Color.White.copy(alpha = 0.8f)),
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = media.releaseDate.take(4).ifEmpty { "2024" },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = media.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
        }
    }
}

/** Deterministic two-stop gradient so the same title always gets the same colours. */
private fun posterPalette(seed: String): List<Color> {
    val palettes = listOf(
        listOf(Color(0xFF1A2980), Color(0xFF26D0CE)),
        listOf(Color(0xFF6A0572), Color(0xFFAB1B82)),
        listOf(Color(0xFF0F2027), Color(0xFF2C5364)),
        listOf(Color(0xFFB24592), Color(0xFFF15F79)),
        listOf(Color(0xFF134E5E), Color(0xFF71B280)),
        listOf(Color(0xFF42275A), Color(0xFF734B6D)),
        listOf(Color(0xFFCB356B), Color(0xFFBD3F32)),
        listOf(Color(0xFF000428), Color(0xFF004E92)),
    )
    val index = (seed.hashCode().absoluteValue) % palettes.size
    return palettes[index]
}

@Preview
@Composable
private fun PreviewGeneratedMoviePoster() {
    FamilyFilmAppTheme {
        PosterImage(
            media = Media().copy(
                title = "Midnight Cartographer",
                posterPath = "${DEMO_POSTER_SCHEME}904",
                voteAverage = 8.1f,
                releaseDate = "2022-09-30",
                mediaType = MediaType.MOVIE,
            ),
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(2f / 3f),
        )
    }
}

@Preview
@Composable
private fun PreviewGeneratedTvPoster() {
    FamilyFilmAppTheme {
        PosterImage(
            media = Media().copy(
                title = "Northern Lanterns",
                posterPath = "${DEMO_POSTER_SCHEME}951",
                voteAverage = 8.7f,
                releaseDate = "2023-10-04",
                mediaType = MediaType.TV_SHOW,
            ),
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(2f / 3f),
        )
    }
}
