package com.digitalsolution.familyfilmapp.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.ui.components.BottomBar
import com.digitalsolution.familyfilmapp.ui.components.TopBar

@Composable
fun ProfileScreen(navController: NavController) {

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "ProfileScreen")
        }
    }
}
