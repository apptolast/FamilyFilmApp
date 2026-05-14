package com.apptolast.familyfilmapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.navigation.AppNavigation
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        FamilyFilmAppTheme {
            AppNavigation()
        }
    }
}
