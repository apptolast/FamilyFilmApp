package com.digitalsolution.familyfilmapp.ui.screens.home.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeViewModel
import com.digitalsolution.familyfilmapp.ui.screens.home.screen.components.HomeItem
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val activity = LocalContext.current as? Activity

    BackHandler(true) {
        activity?.finish()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { TopBar() })
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

    var isShowedMaxItem by rememberSaveable { mutableStateOf(false) }
    val animateAlphaUi by animateFloatAsState(
        targetValue = if (isShowedMaxItem) 0.6f else 1f,
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(animateAlphaUi)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColumnFilm(
                title = "Por ver",
                showMaxItem = { isShowedMaxItem = true },
                modifier = Modifier.weight(1f)
            )
            ColumnFilm(
                title = "Vistas",
                showMaxItem = { isShowedMaxItem = true },
                modifier = Modifier.weight(1f)
            )
        }
        FloatingActionButton(
            onClick = logout,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
    AnimatedVisibility(
        visible = isShowedMaxItem,
        enter = fadeIn(
            spring(stiffness = Spring.StiffnessLow)
        ),
        exit = fadeOut(
            spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable { isShowedMaxItem = false },
            contentAlignment = Alignment.Center
        ) {
            HomeItem(
                text = "Mi gato",
                modifier = Modifier.scale(1.7f)
            )
        }
    }
}

@Composable
private fun ColumnFilm(title: String, showMaxItem: () -> Unit, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(15.dp),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
                HomeItem(text = "Do click here", showMaxItem = showMaxItem)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeScreen(navController = NavController(LocalContext.current))
    }
}
