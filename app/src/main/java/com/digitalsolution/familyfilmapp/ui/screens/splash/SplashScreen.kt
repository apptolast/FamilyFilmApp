package com.digitalsolution.familyfilmapp.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.theme.GradientSplashScreenBlue
import com.digitalsolution.familyfilmapp.ui.theme.GradientSplashScreenOrange
import com.digitalsolution.familyfilmapp.ui.theme.GradientSplashScreenPurple

@Composable
fun SplashScreen(navController: NavController) {

    val scaleAnimation: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }

    AnimationSplashContent(
        scaleAnimation = scaleAnimation,
        navigate = navController::navigate,
        durationMillisAnimation = 1000,
        delayScreen = 150L
    )

    ContentSplashScreen(
        scaleAnimation = scaleAnimation
    )
}

@Composable
fun ContentSplashScreen(
    scaleAnimation: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientSplashScreenOrange,
                        GradientSplashScreenPurple,
                        GradientSplashScreenBlue,
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.icons8_kotlin),
                contentDescription = "Logotipo Splash Screen",
                modifier = modifier
                    .size(400.dp)
                    .scale(scale = scaleAnimation.value),
            )
        }
    }
}
