package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.ui.components.MediaTypeBadge
import com.apptolast.familyfilmapp.network.TmdbConfig
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun MediaItem(
    media: Media,
    modifier: Modifier = Modifier,
    status: MediaStatus? = null,
    showBadge: Boolean = false,
    onClick: (Media) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2 / 3.2f)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { onClick(media) },
    ) {
        AsyncImage(
            model = if (media.posterPath.isEmpty()) {
                TmdbConfig.PLACEHOLDER_URL
            } else {
                "${TmdbConfig.POSTER_GRID}${media.posterPath}"
            },
            contentDescription = media.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        AnimatedVisibility(status != null) {
            Icon(
                imageVector = if (status == MediaStatus.Watched) {
                    Icons.Default.Visibility
                } else {
                    Icons.Default.PlaylistAddCheckCircle
                },
                contentDescription = Icons.Default.Favorite.toString(),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart),
            )
        }
        if (showBadge) {
            MediaTypeBadge(
                mediaType = media.mediaType,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaItem() {
    FamilyFilmAppTheme {
        MediaItem(
            Media().copy(
                title = "title",
                posterPath = "https:///600x400/000/fff",
            ),
        )
    }
}
