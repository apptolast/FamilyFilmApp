package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopBar()
        }
    ) { paddingValues ->
        HomeContent(innerPadding = paddingValues)
    }
}

@Composable
fun HomeContent(innerPadding: PaddingValues) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
    ) {
        ColumnFilm()
        ColumnFilm()
    }

}

@Composable
private fun ColumnFilm() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.Red),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login Screen")
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeScreen()
    }
}