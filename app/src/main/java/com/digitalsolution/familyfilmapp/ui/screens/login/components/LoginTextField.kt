package com.digitalsolution.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoginTextField(
    textFieldState: String,
    changeTextFieldState: (String) -> Unit,
    labelText: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit) = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = textFieldState,
        onValueChange = changeTextFieldState,
        modifier = modifier.padding(top = 12.dp),
        trailingIcon = trailingIcon,
        label = { Text(text = labelText, color = Color.Gray) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.DarkGray, containerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun LoginTextFieldPreview() {
    FamilyFilmAppTheme {
        LoginTextField(
            textFieldState = "",
            changeTextFieldState = {},
            labelText = "",
            keyboardOptions = KeyboardOptions.Default
        )
    }
}

