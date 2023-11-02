package com.digitalsolution.familyfilmapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.ui.theme.secondarySemiBoldHeadLineS

@Composable
fun CustomSmallButton(
    onClick: () -> Unit, @StringRes text: Int, enabled: Boolean,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(150.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            disabledBackgroundColor = Color(212, 249, 121),
            disabledContentColor = Color(0x80FFFFFF),
        ),
        enabled = enabled,

        ) {
        Text(
            text = stringResource(id = text),
            style = secondarySemiBoldHeadLineS,
            color = Color(255, 255, 255),
        )
    }
}
