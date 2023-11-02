package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardHandlerDivider(
    modifier: Modifier = Modifier,
) {
    Divider(
        color = Color(0xFFD9D9D9),
        thickness = 6.dp,
        modifier = modifier
            .width(122.dp)
            .clip(RoundedCornerShape(50.dp)),
    )
}
