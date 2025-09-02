package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Provider
import com.apptolast.familyfilmapp.ui.screens.home.BASE_URL
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ProvidersContent(
    streamProviders: List<Provider> = emptyList(),
    buyProviders: List<Provider> = emptyList(),
    rentProviders: List<Provider> = emptyList(),
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Stream Providers Section
        if (streamProviders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.provider_available_stream),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProvidersRow(providers = streamProviders)
        }

        // Buy Providers Section
        if (buyProviders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.provider_available_buy),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProvidersRow(providers = buyProviders)
        }

        // Rent Providers Section
        if (rentProviders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.provider_available_rent),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProvidersRow(providers = rentProviders)
        }
    }
}

@Composable
fun ProvidersRow(providers: List<Provider>) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(providers) { provider ->
            ProviderItem(provider = provider)
        }
    }
}

@Composable
fun ProviderItem(provider: Provider) {
    Card(
        modifier = Modifier
            .size(75.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp),
//                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = if (provider.logoPath.isEmpty()) {
                        "https://picsum.photos/100/100"
                    } else {
                        "${BASE_URL}${provider.logoPath}"
                    },
                    contentDescription = provider.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(73.dp)
                        .clip(RoundedCornerShape(10.dp)),
                )
            }

//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = provider.name,
//                style = MaterialTheme.typography.bodySmall,
//                textAlign = TextAlign.Center,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier.fillMaxWidth(),
//            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ProvidersContentPreview() {
    val mockProviders = listOf(
        Provider(
            providerId = 8,
            name = "Netflix",
            logoPath = "/t2yyOv40HZeVlLjYsCsPHnWLk4W.jpg",
        ),
        Provider(
            providerId = 119,
            name = "Amazon Prime Video",
            logoPath = "/68MNrwlkpF7WnmNPXLah69CR5cb.jpg",
        ),
        Provider(
            providerId = 337,
            name = "Disney Plus",
            logoPath = "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg",
        ),
        Provider(
            providerId = 2,
            name = "Apple TV",
            logoPath = "/peURlLlr8jggOwK53fJ5wdQl05y.jpg",
        ),
    )

    FamilyFilmAppTheme {
        ProvidersContent(buyProviders = mockProviders)
    }
}
