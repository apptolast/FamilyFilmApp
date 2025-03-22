package com.apptolast.familyfilmapp.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.components.dialogs.DeleteAccountDialog
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onClickNav: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val provider by viewModel.provider.collectAsStateWithLifecycle()

    // State for showing the delete account dialog
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.screen_title_profile))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.toString(),
                        )
                    }
                },
            )
        },
//        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->

        ProfileContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            firebaseUser = (authState as AuthState.Authenticated).user,
            onLogout = { viewModel.logOut() },
            onDeleteAccount = {
                // Show dialog only when the user has used email/pass provider
                // Delete user straight away if user has used google provider
                when (provider) {
                    GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> viewModel.deleteUser()
                    EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> showDeleteDialog = true
                }
            },
        )

        when (authState) {
            is AuthState.Authenticated -> {


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
                LaunchedEffect(true) {
                    onClickNav()
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    firebaseUser: FirebaseUser? = null,
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
) {

    val photoUrl = firebaseUser?.photoUrl

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Imagen de perfil
        Image(
            painter = if (photoUrl != null) {
                rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = photoUrl.toString())
                        .allowHardware(false)
                        .build(),
                )
            } else {
                painterResource(id = R.drawable.ic_user)
            }, // Agrega tu imagen aquí
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre y correo
        Text(text = firebaseUser?.displayName.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = firebaseUser?.email.toString(), fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones de cuenta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f)),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Opciones de cuenta", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.profile_text_logout),
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre icono y texto
                    Text(text = stringResource(R.string.profile_text_logout))
                }

                Button(
                    onClick = onDeleteAccount,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.profile_text_delete_account),
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre icono y texto
                    Text(text = stringResource(R.string.profile_text_delete_account))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de configuración
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f)),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Configuración", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                SettingItem("Modo oscuro", {})
                SettingItem("Preferencias de películas", {})
                SettingItem("Notificaciones", {})
            }
        }

        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ArrowCircleRight,
            contentDescription = Icons.Default.ArrowCircleRight.toString()
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FamilyFilmAppTheme {
        ProfileContent()
    }
}
