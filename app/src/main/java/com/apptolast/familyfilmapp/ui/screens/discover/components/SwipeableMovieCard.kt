package com.apptolast.familyfilmapp.ui.screens.discover.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Swipeable Movie Card Component
 * Allows user to swipe left (Watched) or right (Want to Watch)
 *
 * @param movie Movie to display
 * @param onSwipeLeft Callback when swiped left (Watched)
 * @param onSwipeRight Callback when swiped right (Want to Watch)
 * @param modifier Modifier
 */
@Composable
fun SwipeableMovieCard(
    movie: Movie,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()
    val swipeThreshold = screenWidth * 0.4f // 40% of screen width to trigger action

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Calculate rotation and alpha based on swipe distance
    val rotation = (offsetX / 20f).coerceIn(-15f, 15f)
    val alpha = (1f - (abs(offsetX) / (screenWidth * 2))).coerceIn(0.5f, 1f)

    // Swipe indicators opacity
    val leftIndicatorAlpha = if (offsetX < 0) (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f) else 0f
    val rightIndicatorAlpha = if (offsetX > 0) (offsetX / swipeThreshold).coerceIn(0f, 1f) else 0f

    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.6f)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .rotate(rotation)
            .graphicsLayer(alpha = alpha)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (abs(offsetX) > swipeThreshold) {
                                // Swipe completed - trigger action
                                val animatable = Animatable(offsetX)
                                val targetX = if (offsetX > 0) screenWidth * 3 else -screenWidth * 3

                                // Animate off screen
                                animatable.animateTo(
                                    targetValue = targetX,
                                    animationSpec = tween(300),
                                ) {
                                    offsetX = value
                                }

                                // Trigger callback
                                if (offsetX > 0) onSwipeRight() else onSwipeLeft()

                                // Reset position
                                offsetX = 0f
                                offsetY = 0f
                            } else {
                                // Snap back to center
                                val animatableX = Animatable(offsetX)
                                val animatableY = Animatable(offsetY)

                                launch {
                                    animatableX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(300),
                                    ) {
                                        offsetX = value
                                    }
                                }
                                launch {
                                    animatableY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(300),
                                    ) {
                                        offsetY = value
                                    }
                                }
                            }
                        }
                    },
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
    ) {
        // Movie Card
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Poster Image
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                // Gradient overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .align(Alignment.BottomCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f),
                                ),
                            ),
                        ),
                )

                // Movie Info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = buildString {
                            append("⭐ ${movie.voteAverage}")
                            movie.releaseDate?.let { append(" • ${it.take(4)}") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Swipe Left Indicator (Watched)
                if (leftIndicatorAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(32.dp)
                            .alpha(leftIndicatorAlpha)
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.85f),
                                shape = androidx.compose.foundation.shape.CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Watched",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }

                // Swipe Right Indicator (Want to Watch)
                if (rightIndicatorAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(32.dp)
                            .alpha(rightIndicatorAlpha)
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.85f),
                                shape = androidx.compose.foundation.shape.CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = "Want to Watch",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
            }
        }
    }
}
