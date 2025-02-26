package com.apptolast.familyfilmapp.ui.screens.profile

import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.shared_viewmodel.AuthState
import com.apptolast.familyfilmapp.ui.shared_viewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
//    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        contentColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->

        when (authState) {
            is AuthState.Authenticated -> {
                ProfileContent(
                    email = (authState as AuthState.Authenticated).user.email ?: "",
                    onClickLogOut = {
                        viewModel.logOut()

//                        navController.navigate(Routes.Login.routes) {
//                            popUpTo(navController.graph.id) {
//                                inclusive = false
//                            }
//                            launchSingleTop = true
//                        }
                    },
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }

            AuthState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            AuthState.Unauthenticated -> { /* no-op */
            }
        }

    }
}

@Composable
fun ProfileContent(email: String, modifier: Modifier = Modifier, onClickLogOut: () -> Unit = {}) {
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
                    text = email,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(
                    modifier = Modifier.height(filedSpacer),
                )

//                Text(
//                    text = "${state.userData.groupIds.size} Groups",
//                    style = MaterialTheme.typography.titleMedium,
//                )

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
            email = "test@test.com",
        )
    }
}
