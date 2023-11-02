package com.digitalsolution.familyfilmapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.digitalsolution.familyfilmapp.ui.theme.secondarySemiBoldHeadLineM

@Composable
fun CardTitle(@StringRes text: Int) {
    Text(
        text = stringResource(id = text),
        style = secondarySemiBoldHeadLineM,
    )
}
