package com.digitalsolution.familyfilmapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val Black: Color = Color(0xFF000000)
val White: Color = Color(0xFFFFFFFF)

val Gray90: Color = Color(0xFF333333)
val Gray85: Color = Color(0xFFD9D9D9)
val Gray80: Color = Color(0xFF666666)
val Gray60: Color = Color(0xFF999999)

val OceanBlue: Color = Color(0xFF0061a2)
val TranquilAzure: Color = Color(0xFFddf2ff)

val ForestGreen: Color = Color(0xFF53a653)

val CrimsonFire: Color = Color(0xFFb00020)

val FFAColorsLight = FFAColors()
val FFAColorsDark = FFAColors()

data class FFAColors(
    val neutral: FFANeutral = FFANeutral(),
    val primary: FFAPrimary = FFAPrimary(),
    val secondary: FFASecondary = FFASecondary(),
    val success: FFASuccess = FFASuccess(),
    val error: FFAError = FFAError(),
)

data class FFANeutral(
    val textTitle: Color = Black,
    val textBody: Color = Gray90,
    val textMedium: Color = Gray80,
    val textDisabled: Color = Gray60,
    val backgroundDefault: Color = White,
    val backgroundWeak: Color = Gray85,
    val borderHeavy: Color = Black,
    val borderDefault: Color = Gray60,
    val iconDefault: Color = Gray90,
)

data class FFAPrimary(
    val textDefault: Color = OceanBlue,
    val backgroundDefault: Color = OceanBlue,
    val backgroundWeak: Color = TranquilAzure,
)

data class FFASecondary(
    val textDefault: Color = White,
    val textBody: Color = White,
    val backgroundDefault: Color = Black,
    val iconDefault: Color = White,
)

data class FFASuccess(
    val textDefault: Color = ForestGreen,
    val backgroundDefault: Color = ForestGreen,
    val iconDefault: Color = ForestGreen,
)

data class FFAError(
    val textDefault: Color = CrimsonFire,
    val backgroundDefault: Color = CrimsonFire,
    val iconDefault: Color = CrimsonFire,
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FFAColorsPreview() {
    Column {
        Text(text = "Neutral", style = FFATheme.typography.body.bodyM)
        Row {
            FFAColorPreviewItem(color = FFATheme.colors.neutral.textTitle, name = "textTitle")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.textBody, name = "textBody")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.textMedium, name = "textMedium")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.textDisabled, name = "textDisabled")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.backgroundDefault, name = "backgroundDefault")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.backgroundWeak, name = "backgroundWeak")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.borderHeavy, name = "borderHeavy")
            FFAColorPreviewItem(color = FFATheme.colors.neutral.borderDefault, name = "borderDefault")
        }
        Text(text = "Primary", style = FFATheme.typography.body.bodyM)
        Row {
            FFAColorPreviewItem(color = FFATheme.colors.primary.textDefault, name = "textDefault")
            FFAColorPreviewItem(color = FFATheme.colors.primary.backgroundDefault, name = "backgroundDefault")
            FFAColorPreviewItem(color = FFATheme.colors.primary.backgroundWeak, name = "backgroundWeak")
        }
        Text(text = "Secondary", style = FFATheme.typography.body.bodyM)
        Row {
            FFAColorPreviewItem(color = FFATheme.colors.secondary.textDefault, name = "textDefault")
            FFAColorPreviewItem(color = FFATheme.colors.secondary.textBody, name = "textBody")
            FFAColorPreviewItem(color = FFATheme.colors.secondary.backgroundDefault, name = "backgroundDefault")
            FFAColorPreviewItem(color = FFATheme.colors.secondary.iconDefault, name = "iconDefault")
        }
        Text(text = "Success", style = FFATheme.typography.body.bodyM)
        Row {
            FFAColorPreviewItem(color = FFATheme.colors.success.textDefault, name = "textDefault")
            FFAColorPreviewItem(color = FFATheme.colors.success.backgroundDefault, name = "backgroundDefault")
            FFAColorPreviewItem(color = FFATheme.colors.success.iconDefault, name = "iconDefault")
        }
        Text(text = "Error", style = FFATheme.typography.body.bodyM)
        Row {
            FFAColorPreviewItem(color = FFATheme.colors.error.textDefault, name = "textDefault")
            FFAColorPreviewItem(color = FFATheme.colors.error.backgroundDefault, name = "backgroundDefault")
            FFAColorPreviewItem(color = FFATheme.colors.error.iconDefault, name = "iconDefault")
        }
    }
}

@Composable
private fun FFAColorPreviewItem(color: Color, name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(color),
    ) {
        Text(text = name, style = FFATheme.typography.body.bodyM)
    }
}
