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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun GoogleButtonContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = com.apptolast.familyfilmapp.R.drawable.ic_google),
            contentDescription = com.apptolast.familyfilmapp.R.drawable.ic_google.toString(),
            modifier = Modifier
                .size(32.dp)
                .padding(horizontal = 5.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(com.apptolast.familyfilmapp.R.string.login_text_sign_in_with_google))
    }
}
