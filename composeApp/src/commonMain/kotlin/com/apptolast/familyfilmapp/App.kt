package com.apptolast.familyfilmapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import org.koin.compose.KoinContext

// Root composable. KoinContext picks up the Koin instance that initKoin()
// already started from the platform entry point (FamilyFilmApp.onCreate on
// Android, iOSApp.init on iOS), so screens can call koinInject() and
// koinViewModel() once block 12 introduces ViewModels and the navigation
// graph.
@Composable
@Preview
fun App() {
    KoinContext {
        FamilyFilmAppTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Family Film — KMP migration in progress")
            }
        }
    }
}
