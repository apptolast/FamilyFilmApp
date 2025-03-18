package com.apptolast.familyfilmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.apptolast.familyfilmapp.navigation.AppNavigation
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Fill the screen
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(
                    arrayListOf(
                        // Add test devices
                        "6EBCD242716D331EAA6673852DA6C4FA", // Androidzen
                    ),
                ).build(),
            )
            MobileAds.initialize(this@MainActivity) { initializationStatus ->
                Timber.d("Admob initialized")
            }
        }

        setContent {
            FamilyFilmAppTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
