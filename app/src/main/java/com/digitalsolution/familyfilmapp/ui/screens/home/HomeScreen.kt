package com.digitalsolution.familyfilmapp.ui.screens.home

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.screens.home.components.HomeItem
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.launch

val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val activity = LocalContext.current as? Activity

    val loginState by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(true) {
        activity?.finish()
    }

    LaunchedEffect(key1 = loginState) {
        if (!loginState) {
            navController.navigateUp()
        }
    }

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = listOf(Icons.Default.AspectRatio, Icons.Default.Clear, Icons.Default.Call)
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item, contentDescription = null) },
                        label = { Text(item.name) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(openDrawer = { scope.launch { drawerState.open() } })
            }
        ) { paddingValues ->
            HomeContent(
                modifier = Modifier.padding(paddingValues),
                logout = viewModel::logout
            )
        }
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
        Column(modifier = Modifier.fillMaxSize()) {
            RowFilm(
                title = "Por ver",
                showMaxItem = { isShowedMaxItem = true },
                modifier = Modifier.weight(1f)
            )
            RowFilm(
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
            modifier = Modifier
                .fillMaxSize()
                .clickable { isShowedMaxItem = false },
            contentAlignment = Alignment.Center
        ) {
            val random by rememberSaveable { mutableIntStateOf(list.random()) }
            HomeItem(
                text = "Mi gato",
                number = random,
                modifier = Modifier.scale(1.7f),
            )
        }
    }
}

@Composable
private fun RowFilm(title: String, showMaxItem: () -> Unit, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier.padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 25.dp),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(list) { number ->
                HomeItem(text = "Do click here", number = number, showMaxItem = showMaxItem)
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
