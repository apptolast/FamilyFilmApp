package com.apptolast.familyfilmapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val activityHolder: CurrentActivityHolder by inject()
    private val analyticsTracker: AnalyticsTracker by inject()
    private val requestNotificationsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        analyticsTracker.logEvent(
            "notification_permission_result",
            mapOf("granted" to granted),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        activityHolder.attach(this)
        requestPostNotificationsPermission()
        (application as FamilyFilmApp).gatherConsentAndInitializeAds(this, analyticsTracker)
        setContent {
            App()
        }
    }

    override fun onDestroy() {
        activityHolder.detach(this)
        super.onDestroy()
    }

    private fun requestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) return

        requestNotificationsPermission.launch(permission)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
