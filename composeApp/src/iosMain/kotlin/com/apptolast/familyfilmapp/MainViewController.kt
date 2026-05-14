package com.apptolast.familyfilmapp

import androidx.compose.ui.window.ComposeUIViewController

// Entry point for the SwiftUI side. iosApp/iosApp/iOSApp.swift calls
// initKoinForIos() (see di/KoinInit.kt) before this composable mounts.
fun MainViewController() = ComposeUIViewController { App() }
