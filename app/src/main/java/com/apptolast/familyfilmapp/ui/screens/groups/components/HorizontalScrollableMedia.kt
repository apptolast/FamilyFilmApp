package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.ui.screens.home.MediaItem
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HorizontalScrollableMedia(mediaList: List<Media>, onMediaClick: (Media) -> Unit = {}) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(mediaList) { media ->
            MediaItem(
                media = media,
                modifier = Modifier.width(130.dp),
                onClick = onMediaClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalScrollableMedia() {
    FamilyFilmAppTheme {
        HorizontalScrollableMedia(
            mediaList = listOf(
                Media().copy(id = 1, title = "Title 1", overview = "Description 1"),
                Media().copy(id = 2, title = "Title 2", overview = "Description 2"),
                Media().copy(id = 3, title = "Title 3", overview = "Description 3"),
            ),
        )
    }
}
