package com.digitalsolution.familyfilmapp.ui.screens.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.ui.screens.home.components.CustomCard
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun RecommendScreen(navController: NavController) {

    val list = listOf(
        "Terror",
        "Comedia",
        "Romántico",
        "Ciencia Ficción",
        "Fantasía",
    )
    val list2 = listOf(
        "Acción",
        "Drama",
        "Anime",
        "Aventura",
        "Musical"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyRow {
            items(list) { item ->
                AssistChip(
                    onClick = { /*TODO*/ },
                    label = { Text(text = item) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
        LazyRow {
            items(list2) { item ->
                AssistChip(
                    onClick = { /*TODO*/ },
                    label = { Text(text = item) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Text(
            text = "Comedy",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier
                .padding(10.dp)
                .padding(bottom = 4.dp)
        )

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(listSize.toList()) { number ->
                CustomCard(
                    // TODO: Add the clickable property to the modifier
                    modifier = Modifier.padding(5.dp)
                ) {
                    Column {

                        AsyncImage(
                            model = "https://loremflickr.com/600/600/cat?lock=$number",
                            contentDescription = null,
                        )
                        Text(
                            text = "Film ${number + 1}",
                            modifier = Modifier
                                .padding(10.dp)
                                .padding(bottom = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private val listSize = 0..24

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RecommendScreenPreview() {
    FamilyFilmAppTheme {
        RecommendScreen(navController = rememberNavController())
    }
}
