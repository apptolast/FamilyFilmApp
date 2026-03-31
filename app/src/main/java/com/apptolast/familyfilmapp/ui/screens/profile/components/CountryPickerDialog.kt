package com.apptolast.familyfilmapp.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.utils.TMDB_REGIONS
import com.apptolast.familyfilmapp.utils.TmdbRegion
import com.apptolast.familyfilmapp.utils.countryCodeToFlag
import java.util.Locale

@Composable
fun CountryPickerDialog(
    currentCountryCode: String,
    onSelectRegion: (TmdbRegion) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val sortedRegions = TMDB_REGIONS.sortedBy { region ->
        Locale("", region.countryCode).getDisplayCountry(Locale.getDefault())
    }

    val filteredRegions = if (searchQuery.isBlank()) {
        sortedRegions
    } else {
        sortedRegions.filter { region ->
            val displayName = Locale("", region.countryCode)
                .getDisplayCountry(Locale.getDefault())
            displayName.contains(searchQuery, ignoreCase = true) ||
                region.countryCode.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text(stringResource(R.string.region_select_title))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(R.string.region_search_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(filteredRegions) { region ->
                        val displayName = Locale.Builder()
                            .setRegion(region.countryCode).build()
                            .getDisplayCountry(Locale.getDefault())
                        val flag = countryCodeToFlag(region.countryCode)
                        val isSelected = region.countryCode.equals(currentCountryCode, ignoreCase = true)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectRegion(region)
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = flag,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}
