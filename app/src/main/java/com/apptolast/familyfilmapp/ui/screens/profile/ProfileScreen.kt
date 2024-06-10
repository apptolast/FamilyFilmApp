package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val profileUiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->
        ProfileContent(
            profileUiState,
            onClickLogOut = {
                viewModel.logOut()

                navController.navigate(Routes.Login.routes) {
                    popUpTo(navController.graph.id) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun ProfileContent(profileUiState: ProfileUiState, onClickLogOut: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card {
            Column(
                modifier = Modifier.padding(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
//                    if (profileUiState.userData.photo.isNotBlank()) {
//                        AsyncImage(
//                            model = profileUiState.userData.photo,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(100.dp)
//                                .clip(RoundedCornerShape(50.dp)),
//                        )
//                    }
//                    if (profileUiState.userData.name.isNotBlank()) Text(text = profileUiState.userData.name)
                    Text(text = profileUiState.userData.email)
                }

                Text(
                    text = "6 Grupos",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )

                Button(onClick = { onClickLogOut() }, modifier = Modifier.padding(top = 80.dp)) {
                    Text(text = "Logout")
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                }

                Button(onClick = { onClickLogOut() }, modifier = Modifier.padding(top = 80.dp)) {
                    Text(text = "Delete User")
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FamilyFilmAppTheme {
        ProfileScreen(navController = rememberNavController())
    }
}
