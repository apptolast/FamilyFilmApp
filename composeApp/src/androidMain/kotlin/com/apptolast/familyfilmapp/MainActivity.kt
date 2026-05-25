package com.apptolast.familyfilmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val activityHolder: CurrentActivityHolder by inject()
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        activityHolder.attach(this)
        (application as FamilyFilmApp).gatherConsentAndInitializeAds(this, analyticsTracker)
        setContent {
            App()
        }
    }

    override fun onDestroy() {
        activityHolder.detach(this)
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
