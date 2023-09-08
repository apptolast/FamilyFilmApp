package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.navigation.Routes
import com.digitalsolution.familyfilmapp.popUpToNavigate
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    if (viewModel.state.value) {
        LaunchedEffect(key1 = true) {
            navController.popUpToNavigate(Routes.Login.routes, Routes.Home.routes)
        }
    }

    Scaffold(
        topBar = { TopBar() }
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            onClickLogout = { viewModel.logout() }
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    onClickLogout: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Button(onClick = onClickLogout) {
            Text(text = "Logout")
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColumnFilm(modifier)
            ColumnFilm(modifier)
        }
    }
}

@Composable
private fun ColumnFilm(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Text(text = "Login Screen", color = Color.Red)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeContent {}
    }
}
