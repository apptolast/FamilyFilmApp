package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.digitalsolution.familyfilmapp.ui.theme.white40Opacity

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CardLoginMain(
    textFieldEmailState: String,
    changeEmailState: (String) -> Unit,
    textPasswordState: String,
    changePasswordState: (String) -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = white40Opacity)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_film_family),
                contentDescription = stringResource(R.string.snail_logo),
                modifier = Modifier
                    .width(134.dp)
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.film_family),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.shared_your_films_with_your_family_and_friends),
                style = MaterialTheme.typography.titleMedium
            )
            TextField(
                value = textFieldEmailState,
                onValueChange = changeEmailState,
                modifier = Modifier.padding(vertical = 4.dp),
                label = { Text(text = stringResource(R.string.enter_your_email), color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.DarkGray,
                    containerColor = Color.White
                )
            )
            TextField(
                value = textPasswordState,
                onValueChange = changePasswordState,
                modifier = Modifier.padding(vertical = 4.dp),
                label = { Text(text = stringResource(R.string.enter_your_password), color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.DarkGray,
                    containerColor = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardLoginMainPreview() {
    FamilyFilmAppTheme {
        CardLoginMain(textFieldEmailState = "",
            textPasswordState = "",
            changeEmailState = {},
            changePasswordState = {})
    }
}
