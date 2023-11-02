package com.digitalsolution.familyfilmapp.ui.screens.groups.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

const val CARD_HEIGHT = 0.67

@Composable
fun BottomSheetGroupScreen(
    hideModal: () -> Unit,
) {
    BackHandler {
        hideModal()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height((LocalConfiguration.current.screenHeightDp * CARD_HEIGHT).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(255, 224, 126),
        ),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
    ) {
        BottomSheetGroupScreenContent()
    }
}
