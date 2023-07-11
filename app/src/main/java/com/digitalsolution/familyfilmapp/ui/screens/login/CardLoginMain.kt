package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.getGoogleFontFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CardLoginMain(
    textFieldEmailState: String,
    changeEmailState: (String) -> Unit,
    textPasswordState: String,
    changePasswordState: (String) -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_film_family),
                contentDescription = "Snail Logo",
                modifier = Modifier
                    .width(134.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Film Family",
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
                fontFamily = "Alfa Slab One".getGoogleFontFamily(),
                fontSize = 36.sp
            )
            Text(
                text = "Shared your films with your family and friends",
                fontFamily = "Anton".getGoogleFontFamily(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = textFieldEmailState,
                onValueChange = changeEmailState,
                modifier = Modifier.padding(vertical = 4.dp),
                label = { Text(text = "Enter your email", color = Color.Gray) }
            )
            OutlinedTextField(
                value = textPasswordState,
                onValueChange = changePasswordState,
                modifier = Modifier.padding(vertical = 4.dp),
                label = { Text(text = "Enter your password", color = Color.Gray) }
            )
        }
    }
}
