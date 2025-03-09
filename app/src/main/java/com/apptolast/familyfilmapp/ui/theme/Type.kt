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

val headlineLargeFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Aclonica"),
        fontProvider = provider,
    ),
)

val titleLargeFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Acme"),
        fontProvider = provider,
    ),
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    headlineLarge = baseline.headlineLarge.copy(fontFamily = headlineLargeFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = titleLargeFontFamily),
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
