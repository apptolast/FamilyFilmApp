package com.apptolast.familyfilmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Fill the screen
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}
