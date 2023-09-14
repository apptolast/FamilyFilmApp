package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrawerItems(val icon: ImageVector, val title: String, val onClick: () -> Unit) {
    object Groups : DrawerItems(icon = Icons.Default.Groups, title = "Groups", onClick = {})
}