package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWrapper(title: String, onClickLogOut: () -> Unit) {
    val customTopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.outlineVariant,
    )
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
        actions = {
            Icon(
                imageVector = Icons.Filled.Logout,
                contentDescription = "Settings icon",
                modifier = Modifier.clickable {
                    onClickLogOut()
                },
            )
        },
        colors = customTopAppBarColors,
    )
}

@Preview
@Composable
private fun TopAppBarWrapperPreview() {
    FamilyFilmAppTheme {
        TopAppBarWrapper("Title") {}
    }
}
