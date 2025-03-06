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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.components.dialogs.DeleteAccountDialog
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    // State for showing the delete account dialog
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        contentColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->

        when (authState) {
            is AuthState.Authenticated -> {
                ProfileContent(
                    email = (authState as AuthState.Authenticated).user.email ?: "",
                    modifier = Modifier.padding(paddingValues),
                    onClickLogOut = { viewModel.logOut() },
                    onDeleteUser = {
                        // Show dialog only when the user has used email/pass provider
                        // Delete user straight away if user has used google provider
                        if (viewModel.credential != null) {
                            viewModel.deleteUser()
                        } else {
                            showDeleteDialog = true
                        }
                    },
                )

                // Show delete account dialog if state is true
                if (showDeleteDialog) {
                    DeleteAccountDialog(
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = { email, password ->
                            viewModel.deleteUser(email, password)
                            showDeleteDialog = false
                        },
                    )
                }
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

            AuthState.Unauthenticated -> {
                navController.navigate(Routes.Login.routes)
            }
        }
    }
}

@Composable
fun ProfileContent(
    email: String,
    modifier: Modifier = Modifier,
    onClickLogOut: () -> Unit = {},
    onDeleteUser: () -> Unit = {},
) {
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
                    style = MaterialTheme.typography.titleSmall,
                )

                Spacer(
                    modifier = Modifier.height(filedSpacer),
                )

                Spacer(modifier = Modifier.height(filedSpacer))

                Button(onClick = onClickLogOut) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.width(filedSpacer))
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                }

                Spacer(modifier = Modifier.height(filedSpacer))

                Button(
                    onClick = onDeleteUser,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.profile_delete_account),
                        style = MaterialTheme.typography.titleSmall,
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
