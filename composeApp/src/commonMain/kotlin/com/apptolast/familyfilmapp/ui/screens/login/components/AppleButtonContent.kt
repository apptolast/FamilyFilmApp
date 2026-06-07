package com.apptolast.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.ic_apple
import familyfilmkmp.composeapp.generated.resources.login_text_sign_in_with_apple
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppleButtonContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_apple),
            contentDescription = "Apple logo",
            modifier = Modifier
                .size(32.dp)
                .padding(horizontal = 5.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(Res.string.login_text_sign_in_with_apple))
    }
}
