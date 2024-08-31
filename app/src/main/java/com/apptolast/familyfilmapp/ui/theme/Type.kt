package com.apptolast.familyfilmapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Acme"),
        fontProvider = provider,
    ),
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Aclonica"),
        fontProvider = provider,
    ),
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)

@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
    FamilyFilmAppTheme {
        Column {
            Text(text = "displayLarge", style = MaterialTheme.typography.displayLarge)
            Text(text = "displayLarge", style = MaterialTheme.typography.displayLarge)
            Text(text = "displayMedium", style = MaterialTheme.typography.displayMedium)
            Text(text = "displaySmall", style = MaterialTheme.typography.displaySmall)
            Text(text = "headlineLarge", style = MaterialTheme.typography.headlineLarge)
            Text(text = "headlineMedium", style = MaterialTheme.typography.headlineMedium)
            Text(text = "headlineSmall", style = MaterialTheme.typography.headlineSmall)
            Text(text = "titleLarge", style = MaterialTheme.typography.titleLarge)
            Text(text = "titleMedium", style = MaterialTheme.typography.titleMedium)
            Text(text = "titleSmall", style = MaterialTheme.typography.titleSmall)
            Text(text = "bodyLarge", style = MaterialTheme.typography.bodyLarge)
            Text(text = "bodyMedium", style = MaterialTheme.typography.bodyMedium)
            Text(text = "bodySmall", style = MaterialTheme.typography.bodySmall)
            Text(text = "labelLarge", style = MaterialTheme.typography.labelLarge)
            Text(text = "labelMedium", style = MaterialTheme.typography.labelMedium)
            Text(text = "labelSmall", style = MaterialTheme.typography.labelSmall)
        }
    }
}
