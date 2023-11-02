package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.digitalsolution.familyfilmapp.ui.theme.secondaryRegularBodyL

@Composable
fun CardSubtitle(text: Int) {
    Text(
        text = stringResource(id = text),
        style = secondaryRegularBodyL,
    )
}
