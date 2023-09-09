package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopBar()
        }
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            logout = {
                viewModel.logout()
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "logged",
                    false
                )
                navController.navigateUp()
            }
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    logout: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Button(onClick = logout) {
            Text(text = "Logout")
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColumnFilm(modifier.weight(1f))
            ColumnFilm(modifier.weight(1f))
        }
    }
}

@Composable
private fun ColumnFilm(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .background(Color.Blue)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(text = "Login1 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login2 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login3 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login1 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login2 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login3 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login1 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login2 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login3 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login1 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login2 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login3 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login1 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login2 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = "Login3 Screen", color = Color.Red)
            Spacer(modifier.height(12.dp))
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeContent {}
    }
}
