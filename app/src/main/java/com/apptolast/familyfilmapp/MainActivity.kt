package com.apptolast.familyfilmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.navigation.AppNavigation
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Gather GDPR consent and initialize AdMob SDK (UMP requires Activity context)
        (application as FamilyFilmApp).gatherConsentAndInitializeAds(this)

        setContent {
            FamilyFilmAppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation(analyticsTracker = analyticsTracker)
                }
            }
        }
    }
}
