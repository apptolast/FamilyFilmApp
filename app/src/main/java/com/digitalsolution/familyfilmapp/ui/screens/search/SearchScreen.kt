package com.digitalsolution.familyfilmapp.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.ui.screens.search.components.FilmItem
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme


@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchScreenViewModel = hiltViewModel()
) {

    SearchContent(viewModel)
}


@Composable
fun SearchContent(
    viewModel: SearchScreenViewModel
) {

    var searchText by rememberSaveable { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            onValueChange = { searchText = it },
            shape = RectangleShape,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "")
            }
        )
        LazyColumn {
            items(viewModel.getListFilmFake().toList()) { item ->
                FilmItem(film = item)
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SearchScreenPreview() {
    FamilyFilmAppTheme {
        //SearchContent()
    }
}




