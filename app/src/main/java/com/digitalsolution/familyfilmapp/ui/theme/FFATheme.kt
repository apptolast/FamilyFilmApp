package com.digitalsolution.familyfilmapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalFFAColors = staticCompositionLocalOf {
    FFAColors()
}

val LocalFFATypo = staticCompositionLocalOf {
    FFATypography()
}

@Composable
fun FFATheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    if (darkTheme) {
        CompositionLocalProvider(
            LocalFFAColors provides FFAColorsDark,
            LocalFFATypo provides FFATypo,
            content = content,
        )
    } else {
        CompositionLocalProvider(
            LocalFFAColors provides FFAColorsLight,
            LocalFFATypo provides FFATypo,
            content = content,
        )
    }
}
object FFATheme {
    val colors: FFAColors
        @Composable
        @ReadOnlyComposable
        get() = LocalFFAColors.current
    val typography: FFATypography
        @Composable
        @ReadOnlyComposable
        get() = LocalFFATypo.current
}
