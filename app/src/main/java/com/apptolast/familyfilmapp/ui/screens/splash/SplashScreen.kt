package com.apptolast.familyfilmapp.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.screens.login.LoginViewModel

@Composable
fun SplashScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    val loginUiState by viewModel.state.collectAsStateWithLifecycle()


    val scaleAnimation: Animatable<Float, AnimationVector1D> =
        remember { Animatable(initialValue = 0f) }

    LaunchedEffect(key1 = Unit) {
        if (loginUiState.isLogged == true) {
            navController.navigate(Routes.Home.routes) {
                popUpTo(Routes.Splash.routes) { inclusive = true }
                launchSingleTop = true
            }
        }else if (loginUiState.isLogged == false){
            navController.navigate(Routes.Login.routes) {
                popUpTo(Routes.Splash.routes) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    AnimationSplashContent(
        scaleAnimation = scaleAnimation,
        navigate = {
            navController.navigate(Routes.Home.routes) {
                popUpTo(Routes.Home.routes) {
                    inclusive = true
                }
            }
        },
        durationMillisAnimation = 1000,
        delayScreen = 150L,
    )

    ContentSplashScreen(
        scaleAnimation = scaleAnimation,
    )
}

@Composable
fun ContentSplashScreen(scaleAnimation: Animatable<Float, AnimationVector1D>, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_film_family),
                contentDescription = "Logotipo Splash Screen",
                modifier = Modifier
                    .size(400.dp)
                    .scale(scale = scaleAnimation.value),
            )
        }
    }
}
