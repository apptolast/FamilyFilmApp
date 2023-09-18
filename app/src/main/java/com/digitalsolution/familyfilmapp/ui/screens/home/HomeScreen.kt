package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.navigation.Routes
import com.digitalsolution.familyfilmapp.ui.screens.home.components.HomeItem
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlin.system.exitProcess

//FIXME: This is for UI test
private val listSize = 0..12

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val loginState by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(true) {
        exitProcess(0)
    }

    LaunchedEffect(key1 = loginState) {
        if (!loginState) {
            navController.navigateUp()
        }
    }

    HomeContent { navController.navigate(Routes.Details.routes) }
}

@Composable
fun HomeContent(navigateToDetailsScreen: () -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {
        RowFilm(
            title = stringResource(R.string.home_text_my_list),
            icon = Icons.Default.ListAlt,
            navigateToDetailsScreen = navigateToDetailsScreen,
            modifier = Modifier.weight(1f)
        )
        RowFilm(
            title = stringResource(R.string.home_text_seen),
            icon = Icons.Default.Visibility,
            navigateToDetailsScreen = navigateToDetailsScreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RowFilm(
    title: String,
    icon: ImageVector,
    navigateToDetailsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(start = 22.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light)
            )
        }
        LazyRow {
            items(listSize.toList()) { number ->
                HomeItem(
                    text = "Do click here",
                    number = number,
                    navigateToDetailsScreen = navigateToDetailsScreen,
                )
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
