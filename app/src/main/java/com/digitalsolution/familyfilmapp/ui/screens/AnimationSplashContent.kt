package com.digitalsolution.familyfilmapp.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun AnimationSplashContent(
    scaleAnimation: Animatable<Float, AnimationVector1D>,
    navController: NavController,
    durationMillisAnimation: Int,
    delayScreen: Long
) {

    LaunchedEffect(key1 = true) {
        scaleAnimation.animateTo(
            targetValue = 0.5F,
            animationSpec = tween(
                durationMillis = durationMillisAnimation,
                easing = {
                    OvershootInterpolator(3F).getInterpolation(it)
                }
            )
        )

        delay(timeMillis = delayScreen)

        navController.navigate(
            route = Routes.SplashScreenDest.routes
        ) {
            popUpTo(
                route = Routes.Login.routes
            ) {
                inclusive = true
            }
        }
    }
}