package com.digitalsolution.familyfilmapp.ui.screens.search.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.model.local.FilmSearchData

@Composable
fun FilmItem(
    film: FilmSearchData,
    modifier: Modifier = Modifier,
) {

    Row {
        AsyncImage(
            model = film.img,
            contentDescription = null,
            modifier = Modifier.size(width = 180.dp, height = 140.dp)
        )
        Text(
            text = film.title,
            modifier = Modifier
                .padding(10.dp)
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Justify
        )
    }
}