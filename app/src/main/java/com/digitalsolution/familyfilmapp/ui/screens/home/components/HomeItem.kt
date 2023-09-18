package com.digitalsolution.familyfilmapp.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HomeItem(
    text: String,
    number: Int,
    navigateToDetailsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 17.dp)
            .clickable { navigateToDetailsScreen() },
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column {
//            FIXME: This is for test
            AsyncImage(
                model = "https://loremflickr.com/400/400/cat?lock=$number",
                contentDescription = null,
                modifier = Modifier.size(170.dp)
            )
            Text(
                text = text,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeItemPreview() {
    FamilyFilmAppTheme {
        HomeItem(text = "", number = 0, navigateToDetailsScreen = {})
    }
}
