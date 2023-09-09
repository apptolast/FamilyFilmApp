package com.digitalsolution.familyfilmapp.ui.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
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

    val activity = (LocalContext.current as? Activity)
    BackHandler(true) {
        activity?.finish()
    }

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
            ColumnFilm(Modifier.weight(1f))
            ColumnFilm(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ColumnFilm(modifier: Modifier = Modifier) {
    val textLoginOne = "Login1 Screen"
    val textLoginTwo = "Login2 Screen"
    val textLoginThree = "Login3 Screen"

    LazyColumn(
        modifier = modifier
            .background(Color.Blue)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(text = textLoginOne, color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = textLoginTwo, color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = textLoginThree, color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = textLoginOne, color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = textLoginTwo, color = Color.Red)
            Spacer(modifier.height(12.dp))
            Text(text = textLoginThree, color = Color.Red)
            Spacer(modifier.height(12.dp))
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeContent {}
    }
}
