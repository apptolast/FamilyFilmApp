package com.digitalsolution.familyfilmapp.ui.screens.home.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HomeItem(showMaxItem: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(15.dp)
            .clickable { showMaxItem() },
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
//            FIXME: This is for test
            AsyncImage(
                model = "https://loremflickr.com/400/400/cat?lock=1",
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Text(text = "Mi gato", modifier = Modifier.padding(10.dp).padding(bottom = 4.dp))

        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeItem({})
    }
}