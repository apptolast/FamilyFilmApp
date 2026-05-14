package com.apptolast.familyfilmapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.navigation.AppNavigation
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import org.koin.compose.KoinContext

/**
 * Root composable. KoinContext picks up the Koin instance that initKoin()
 * started from the platform entry point (FamilyFilmApp.onCreate on Android,
 * iOSApp.init on iOS). AppNavigation is currently a scaffold that routes to
 * placeholders — block 13 fills each destination with the real screen.
 */
@Composable
@Preview
fun App() {
    KoinContext {
        FamilyFilmAppTheme {
            AppNavigation()
        }
    }
}
