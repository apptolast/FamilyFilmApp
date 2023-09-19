package com.digitalsolution.familyfilmapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun DetailsScreen(navController: NavController) {

    val scrollState = rememberScrollState()
    val localDensity = LocalDensity.current.density

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AsyncImage(
            model = "https://loremflickr.com/400/400/cat?lock=37",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5))
                .height(200.dp - (scrollState.value.dp / localDensity))
                .alpha(1f - (scrollState.value / localDensity) / 170f),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cat's Returns",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "2023")
                Text(text = "1h 34min")
                Text(text = "+18")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                    DetailsButtonContent(icon = Icons.Default.Add, text = "Add to see")
                }
                Spacer(modifier = Modifier.width(14.dp))
                OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                    DetailsButtonContent(icon = Icons.Default.Visibility, text = "Don't seen")
                }
            }
            Text(
                text = "Description",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 15.dp)
            )
            Text(
                text = "Lorem ipsum dolor sit amet consectetur adipiscing elit, nunc nibh erat cum ultrices sociis, nisi facilisi per mollis torquent laoreet. Ultricies tellus cubilia vulputate iaculis varius, aliquam enim velit egestas. Malesuada etiam tempus lectus facilisis nam at ultricies, mus fermentum sollicitudin natoque sodales.\n" +
                        "\n" +
                        "Effendi facilisi integer magna porttitor rutrum sodales aliquet volutpat, porta aenean lectus nisi ridiculus penatibus leo rhoncus semper, sagittis libero dignissim pellentesque urna fames in. Natoque volutpat imperdiet dis quisque cum iaculis, senectus euismod molestie mollis cubilia purus, dictumst cras nec platea dictum. Mollis sodales mus sed ligula condimentum dui aliquet, sociis scelerisque senectus commodo convallis taciti, donec hac gravida cursus id viverra. +\n" +
                        "\n" +
                        "Eleifend facilisi integer magna porttitor rutrum sodales aliquet volutpat, porta aenean lectus nisi ridiculus penatibus leo rhoncus semper, sagittis libero dignissim pellentesque urna fames in. Natoque volutpat imperdiet dis quisque cum iaculis, senectus euismod molestie mollis cubilia purus, dictumst cras nec platea dictum. Mollis sodales mus sed ligula condimentum dui aliquet, sociis scelerisque senectus commodo convallis taciti, donec hac gravida cursus id viverra.",
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .verticalScroll(scrollState)
            )
        }
    }
}

@Composable
private fun DetailsButtonContent(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(text = text)
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DetailsScreenPreview() {
    FamilyFilmAppTheme {
        DetailsScreen(navController = NavController(LocalContext.current))
    }
}