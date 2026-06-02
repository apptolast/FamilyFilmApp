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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Provider
import com.apptolast.familyfilmapp.model.local.visibleTo
import com.apptolast.familyfilmapp.network.TmdbConfig
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.provider_available_buy
import familyfilmkmp.composeapp.generated.resources.provider_available_rent
import familyfilmkmp.composeapp.generated.resources.provider_available_stream
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProvidersContent(
    streamProviders: List<Provider> = emptyList(),
    buyProviders: List<Provider> = emptyList(),
    rentProviders: List<Provider> = emptyList(),
) {
    val visibleStreamProviders = streamProviders.visibleTo()
    val visibleBuyProviders = buyProviders.visibleTo()
    val visibleRentProviders = rentProviders.visibleTo()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (visibleStreamProviders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.provider_available_stream),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProvidersRow(providers = visibleStreamProviders)
        }

        if (visibleBuyProviders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.provider_available_buy),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProvidersRow(providers = visibleBuyProviders)
        }

        if (visibleRentProviders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.provider_available_rent),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProvidersRow(providers = visibleRentProviders)
        }
    }
}

@Composable
fun ProvidersRow(providers: List<Provider>) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = if (provider.logoPath.isEmpty()) {
                        "https://picsum.photos/100/100"
                    } else {
                        "${TmdbConfig.LOGO}${provider.logoPath}"
                    },
                    contentDescription = provider.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewProvidersContent() {
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
    )

    FamilyFilmAppTheme {
        ProvidersContent(buyProviders = mockProviders)
    }
}
