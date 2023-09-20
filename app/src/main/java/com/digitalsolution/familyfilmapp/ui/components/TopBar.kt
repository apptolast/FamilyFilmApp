package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun TopBar(modifier: Modifier = Modifier, onClickLogOut: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "FilmFamily", style = MaterialTheme.typography.headlineMedium)
        Icon(
            imageVector = Icons.Filled.Logout,
            contentDescription = "Settings icon",
            modifier = Modifier.clickable {
                onClickLogOut()
            }
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TopBarPreview() {
    FamilyFilmAppTheme {
        TopBar {}
    }
}
