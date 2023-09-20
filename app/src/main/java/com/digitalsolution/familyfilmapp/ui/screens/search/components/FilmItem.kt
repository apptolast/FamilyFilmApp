package com.digitalsolution.familyfilmapp.ui.screens.search.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.model.local.FilmSearchData
import com.digitalsolution.familyfilmapp.ui.theme.bold

@Composable
fun FilmItem(
    film: FilmSearchData,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = film.img,
            contentDescription = null,
            modifier = Modifier
                .size(width = 180.dp, height = 118.dp)
                .clip(shape = RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,

            )
        Text(
            text = film.title,
            modifier = Modifier
                .padding(10.dp)
                .padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleSmall.bold()
        )
    }
}
