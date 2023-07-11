package com.digitalsolution.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CardLoginsButton(
    text: String,
    backgroundColor: Color,
    paddingVertical: Dp,
//    showSnackBar: () -> Job,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    contentImage: @Composable () -> Unit = {}
) {
    Card(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = paddingVertical),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            contentImage()
            Text(text = text, color = textColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardLoginsButtonPreview() {
    FamilyFilmAppTheme {
        CardLoginsButton(text = "Sign in", backgroundColor = Color.White, paddingVertical = 2.dp)
    }
}
