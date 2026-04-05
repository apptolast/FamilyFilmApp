package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun MediaFilterChips(
    selectedFilter: MediaFilter,
    onFilterSelected: (MediaFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedFilter == MediaFilter.ALL,
            onClick = { onFilterSelected(MediaFilter.ALL) },
            label = { Text(text = stringResource(R.string.filter_all)) },
        )
        FilterChip(
            selected = selectedFilter == MediaFilter.MOVIES,
            onClick = { onFilterSelected(MediaFilter.MOVIES) },
            label = { Text(text = stringResource(R.string.filter_movies)) },
        )
        FilterChip(
            selected = selectedFilter == MediaFilter.TV_SHOWS,
            onClick = { onFilterSelected(MediaFilter.TV_SHOWS) },
            label = { Text(text = stringResource(R.string.filter_tv_shows)) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaFilterChips() {
    FamilyFilmAppTheme {
        MediaFilterChips(
            selectedFilter = MediaFilter.ALL,
            onFilterSelected = {},
        )
    }
}
