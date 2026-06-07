package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.network.TmdbConfig
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.card_sin_borde
import familyfilmkmp.composeapp.generated.resources.group_card_flip_hint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Slow flip on first user tap (Texas Hold'em-style reveal).
private const val FLIP_DURATION_TAP_MS = 700

// Fast flip when re-entering a group where the same recommendation was already revealed.
private const val FLIP_DURATION_AUTO_MS = 250

// Below this angle the card is "still face-down" and above (180 - threshold) it's "fully revealed".
// Clicks are only accepted at rest, avoiding double-taps mid-animation that would navigate before
// the reveal is even visible.
private const val FLIP_NAVIGATE_THRESHOLD_DEG = 170f

private const val CARD_FLIP_CAMERA_DISTANCE = 12f

// Poster aspect (matches MediaItem in the home package).
private const val CARD_ASPECT_RATIO = 2f / 3.2f

@Composable
fun RecommendedMediaFlipCard(
    media: Media,
    isPersistedRevealed: Boolean,
    modifier: Modifier = Modifier,
    onReveal: () -> Unit = {},
    onMediaClick: (Media) -> Unit = {},
) {
    var visiblyRevealed by remember(media.id) { mutableStateOf(false) }
    var pendingDurationMs by remember(media.id) { mutableIntStateOf(FLIP_DURATION_AUTO_MS) }

    LaunchedEffect(isPersistedRevealed, media.id) {
        if (isPersistedRevealed && !visiblyRevealed) {
            pendingDurationMs = FLIP_DURATION_AUTO_MS
            visiblyRevealed = true
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (visiblyRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = pendingDurationMs),
        label = "recommended-card-flip",
    )

    val hint = stringResource(Res.string.group_card_flip_hint)
    val clickEnabled = rotation <= 0f || rotation >= FLIP_NAVIGATE_THRESHOLD_DEG

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(CARD_ASPECT_RATIO)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = CARD_FLIP_CAMERA_DISTANCE * density
            }
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = clickEnabled) {
                if (!visiblyRevealed) {
                    pendingDurationMs = FLIP_DURATION_TAP_MS
                    visiblyRevealed = true
                    onReveal()
                } else {
                    onMediaClick(media)
                }
            }
            .semantics {
                contentDescription = if (visiblyRevealed) media.title else hint
            },
    ) {
        if (rotation < 90f) {
            Image(
                painter = painterResource(Res.drawable.card_sin_borde),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        } else {
            // Re-flip the front face so the rendered content isn't mirrored.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
            ) {
                CardFront(media = media, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun CardFront(media: Media, modifier: Modifier = Modifier) {
    AsyncImage(
        model = if (media.posterPath.isEmpty()) {
            TmdbConfig.PLACEHOLDER_URL
        } else {
            "${TmdbConfig.POSTER_GRID}${media.posterPath}"
        },
        contentDescription = media.title,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Preview
@Composable
private fun PreviewRecommendedMediaFlipCardBack() {
    FamilyFilmAppTheme {
        RecommendedMediaFlipCard(
            media = Media().copy(id = 1, title = "The Punisher: One Last Kill"),
            isPersistedRevealed = false,
            onReveal = {},
            onMediaClick = {},
            modifier = Modifier.fillMaxWidth(0.6f),
        )
    }
}

@Preview
@Composable
private fun PreviewRecommendedMediaFlipCardFront() {
    FamilyFilmAppTheme {
        RecommendedMediaFlipCard(
            media = Media().copy(id = 1, title = "The Punisher: One Last Kill"),
            isPersistedRevealed = true,
            onReveal = {},
            onMediaClick = {},
            modifier = Modifier.fillMaxWidth(0.6f),
        )
    }
}
