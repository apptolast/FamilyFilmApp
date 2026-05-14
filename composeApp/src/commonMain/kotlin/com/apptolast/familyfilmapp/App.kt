package com.apptolast.familyfilmapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

// Placeholder root composable. Block 5 of the migration plan replaces this with
// FamilyFilmAppTheme { AppNavigation() } and wires up Koin, navigation and screens.
@Composable
@Preview
fun App() {
    FamilyFilmAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Family Film — KMP migration in progress")
        }
    }
}
