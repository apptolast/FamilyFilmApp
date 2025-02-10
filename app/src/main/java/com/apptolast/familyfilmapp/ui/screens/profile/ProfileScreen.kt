package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        contentColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        ProfileContent(
            state = state,
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
fun ProfileContent(state: ProfileUiState, modifier: Modifier = Modifier, onClickLogOut: () -> Unit = {}) {
    var filedSpacer = 16.dp

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
//            modifier = Modifier
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = state.userData.email,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(
                    modifier = Modifier.height(filedSpacer),
                )

                Text(
                    text = "${state.userData.groupIds.size} Groups",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(filedSpacer))

                Button(onClick = { onClickLogOut() }) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.width(filedSpacer))
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                }

                Spacer(modifier = Modifier.height(filedSpacer))

                Button(onClick = { onClickLogOut() }) {
                    Text(
                        text = "Delete User",
                        style = MaterialTheme.typography.titleMedium,
                    )
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
        ProfileContent(
            state = ProfileUiState().copy(
                userData = User().copy(
                    email = "test@test.com",
                    groupIds = listOf("id1", "id2"),
                ),
            ),
        )
    }
}
