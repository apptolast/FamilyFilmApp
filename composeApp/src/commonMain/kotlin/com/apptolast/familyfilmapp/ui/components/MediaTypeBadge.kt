package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun MediaTypeBadge(mediaType: MediaType, modifier: Modifier = Modifier) {
    val label = when (mediaType) {
        MediaType.MOVIE -> "FILM"
        MediaType.TV_SHOW -> "TV"
    }
    val color = when (mediaType) {
        MediaType.MOVIE -> MaterialTheme.colorScheme.primary
        MediaType.TV_SHOW -> MaterialTheme.colorScheme.tertiary
    }

    Text(
        text = label,
        color = Color.White,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.85f))
            .padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaTypeBadgeMovie() {
    FamilyFilmAppTheme {
        MediaTypeBadge(mediaType = MediaType.MOVIE)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaTypeBadgeTvShow() {
    FamilyFilmAppTheme {
        MediaTypeBadge(mediaType = MediaType.TV_SHOW)
    }
}
