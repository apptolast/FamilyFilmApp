package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.profile_avatar
import org.jetbrains.compose.resources.painterResource

/**
 * Circular avatar with a surface disc and accent ring, so photos and fallback content stay
 * readable on the near-black chrome.
 */
@Composable
fun ProfileAvatar(
    photoUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    fallbackContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    val placeholder = painterResource(Res.drawable.profile_avatar)
    val hasPhoto = photoUrl.isNotBlank()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(width = borderWidth, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (hasPhoto || fallbackContent == null) {
            AsyncImage(
                // Inset by the border width so the photo never paints over the ring.
                model = photoUrl.takeIf { hasPhoto },
                contentDescription = contentDescription,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .padding(borderWidth)
                    .clip(CircleShape),
            )
        } else {
            fallbackContent()
        }
    }
}

@Preview
@Composable
private fun PreviewProfileAvatarTopBar() {
    FamilyFilmAppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest).padding(16.dp)) {
            ProfileAvatar(photoUrl = "", contentDescription = null, size = 48.dp)
        }
    }
}

@Preview
@Composable
private fun PreviewProfileAvatarLarge() {
    FamilyFilmAppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            ProfileAvatar(photoUrl = "", contentDescription = null, size = 120.dp, borderWidth = 3.dp)
        }
    }
}
