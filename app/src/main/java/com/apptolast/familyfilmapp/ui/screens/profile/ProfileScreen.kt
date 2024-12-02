package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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

    ) { paddingValues ->
        ProfileContent(
            state.userData,
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
fun ProfileContent(user: User, modifier: Modifier = Modifier, onClickLogOut: () -> Unit = {}) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.8f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.large,
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
//                    if (profileUiState.userData.photo.isNotBlank()) {
//                        AsyncImage(
//                            model = profileUiState.userData.photo,
//                            contentDescription = null,
//                            modifier = Modifierº
//                                .size(100.dp)
//                                .clip(RoundedCornerShape(50.dp)),
//                        )
//                    }
//                    if (profileUiState.userData.name.isNotBlank()) Text(text = profileUiState.userData.name)
            Text(
                text = user.email,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.75f)
                    .height(48.dp),
                onClick = { onClickLogOut() },
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = "Logout")
                    Text(
                        text = "Logout",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.75f)
                    .height(48.dp),
                onClick = { onClickLogOut() },
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    Text(
                        text = "Delete User",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                    )
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
            user = User().copy(email = "test@company.com"),
        )
    }
}
