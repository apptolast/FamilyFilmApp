package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_HOME_MOVIE_ITEM
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_FIELD
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_LABEL

@Composable
fun HomeContent(
    state: HomeUiState,
    media: List<Media>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (MediaFilter) -> Unit,
    onMediaSelected: (Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleMedia = if (searchQuery.isBlank()) media else state.filterMedia

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Welcome ${state.user.displayName}",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search", modifier = Modifier.testTag(TT_HOME_SEARCH_TEXT_LABEL)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TT_HOME_SEARCH_TEXT_FIELD),
        )
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MediaFilter.entries.forEach { filter ->
                FilterChip(
                    selected = state.selectedFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(filter.name) },
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(visibleMedia, key = { _, m -> m.id }) { index, item ->
                    MediaGridCard(
                        media = item,
                        modifier = Modifier.testTag("$TT_HOME_MOVIE_ITEM$index"),
                        onClick = { onMediaSelected(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaGridCard(
    media: Media,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = media.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = media.overview,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewHomeContentLoading() {
    FamilyFilmAppTheme {
        HomeContent(
            state = HomeUiState().copy(isLoading = true),
            media = emptyList(),
            searchQuery = "",
            onSearchQueryChange = {},
            onFilterSelected = {},
            onMediaSelected = {},
        )
    }
}

@Composable
@Preview
private fun PreviewHomeContentWithMedia() {
    FamilyFilmAppTheme {
        HomeContent(
            state = HomeUiState().copy(
                user = User(id = "u1", email = "demo@example.com", language = "en-US", photoUrl = ""),
            ),
            media = listOf(
                Media(title = "The Matrix", posterPath = ""),
                Media(title = "Stranger Things", posterPath = ""),
            ),
            searchQuery = "",
            onSearchQueryChange = {},
            onFilterSelected = {},
            onMediaSelected = {},
        )
    }
}
