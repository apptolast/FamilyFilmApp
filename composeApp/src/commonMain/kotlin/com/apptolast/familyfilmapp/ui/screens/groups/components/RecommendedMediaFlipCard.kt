package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.network.TmdbConfig
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.group_card_flip_hint
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

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
    onReveal: () -> Unit,
    onMediaClick: (Media) -> Unit,
    modifier: Modifier = Modifier,
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
            CardBack(hint = hint, modifier = Modifier.fillMaxSize())
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

@Composable
private fun CardBack(hint: String, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier.background(primary),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeColor = onPrimary.copy(alpha = 0.85f)
            val faintColor = onPrimary.copy(alpha = 0.35f)
            val w = size.width
            val h = size.height
            val margin = min(w, h) * 0.06f
            val frameStroke = min(w, h) * 0.012f

            // Outer decorative border (double-line frame).
            drawRect(
                color = strokeColor,
                topLeft = Offset(margin, margin),
                size = androidx.compose.ui.geometry.Size(w - 2 * margin, h - 2 * margin),
                style = Stroke(width = frameStroke),
            )
            val innerInset = margin * 1.6f
            drawRect(
                color = strokeColor,
                topLeft = Offset(innerInset, innerInset),
                size = androidx.compose.ui.geometry.Size(w - 2 * innerInset, h - 2 * innerInset),
                style = Stroke(width = frameStroke * 0.6f),
            )

            // Diamond grid inside the inner frame.
            val gridArea = androidx.compose.ui.geometry.Size(
                width = w - 2 * innerInset,
                height = h - 2 * innerInset,
            )
            val gridOrigin = Offset(innerInset, innerInset)
            val step = min(gridArea.width, gridArea.height) / 12f
            val gridStroke = frameStroke * 0.35f
            // Diagonals going down-right.
            var d = -gridArea.height
            while (d < gridArea.width) {
                drawLine(
                    color = faintColor,
                    start = Offset(gridOrigin.x + d, gridOrigin.y),
                    end = Offset(gridOrigin.x + d + gridArea.height, gridOrigin.y + gridArea.height),
                    strokeWidth = gridStroke,
                )
                d += step
            }
            // Diagonals going down-left.
            d = 0f
            while (d < gridArea.width + gridArea.height) {
                drawLine(
                    color = faintColor,
                    start = Offset(gridOrigin.x + d, gridOrigin.y),
                    end = Offset(gridOrigin.x + d - gridArea.height, gridOrigin.y + gridArea.height),
                    strokeWidth = gridStroke,
                )
                d += step
            }

            // Central diamond rosette.
            val cx = w / 2f
            val cy = h / 2f
            val r = min(w, h) * 0.18f
            val diamond = listOf(
                Offset(cx, cy - r),
                Offset(cx + r, cy),
                Offset(cx, cy + r),
                Offset(cx - r, cy),
            )
            drawLine(strokeColor, diamond[0], diamond[1], strokeWidth = frameStroke)
            drawLine(strokeColor, diamond[1], diamond[2], strokeWidth = frameStroke)
            drawLine(strokeColor, diamond[2], diamond[3], strokeWidth = frameStroke)
            drawLine(strokeColor, diamond[3], diamond[0], strokeWidth = frameStroke)

            val petal = r * 0.45f
            drawCircle(strokeColor, radius = petal, center = Offset(cx, cy - petal), style = Stroke(gridStroke * 1.6f))
            drawCircle(strokeColor, radius = petal, center = Offset(cx + petal, cy), style = Stroke(gridStroke * 1.6f))
            drawCircle(strokeColor, radius = petal, center = Offset(cx, cy + petal), style = Stroke(gridStroke * 1.6f))
            drawCircle(strokeColor, radius = petal, center = Offset(cx - petal, cy), style = Stroke(gridStroke * 1.6f))
            drawCircle(strokeColor, radius = petal * 0.45f, center = Offset(cx, cy))
        }

        // Bottom scrim + hint, mirroring the gradient trick used in SwipeableMediaCard.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primary.copy(alpha = 0.95f),
                        ),
                    ),
                ),
        )
        Text(
            text = hint,
            style = MaterialTheme.typography.titleMedium,
            color = onPrimary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
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
