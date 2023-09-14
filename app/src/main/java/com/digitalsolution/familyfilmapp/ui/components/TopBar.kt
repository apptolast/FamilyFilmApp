package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.Job

@Composable
fun TopBar(openDrawer: () -> Job, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Menu icon",
            modifier = Modifier.clickable { openDrawer() })
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mi Lista", fontSize = 20.sp)
        }
        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings icon")
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TopBarPreview() {
    FamilyFilmAppTheme {
//        TopBar(openDrawer = { /*TODO*/ })
    }
}
