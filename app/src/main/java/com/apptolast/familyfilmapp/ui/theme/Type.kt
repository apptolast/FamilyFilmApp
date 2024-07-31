package com.apptolast.familyfilmapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.apptolast.familyfilmapp.R

// Set of Material typography styles to start with
val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),

    titleLarge = TextStyle(
        fontSize = 26.sp,
        fontFamily = FontFamily(Font(R.font.holtwood_one_sc)),
        fontWeight = FontWeight(200),
        textAlign = TextAlign.Center,
    ),

    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.homenaje)),
        fontWeight = FontWeight(400),
        textAlign = TextAlign.Center,
    ),

    titleSmall = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.open_sans_regular)),
        fontWeight = FontWeight(400),
        textAlign = TextAlign.Center,
    ),
)

fun TextStyle.bold(): TextStyle = this.copy(fontWeight = FontWeight.Bold)

@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
    FamilyFilmAppTheme {
        Column {
            Text(text = "displayLarge", style = Typography.displayLarge)
            Text(text = "displayMedium", style = Typography.displayMedium)
            Text(text = "displaySmall", style = Typography.displaySmall)
            Text(text = "headlineLarge", style = Typography.headlineLarge)
            Text(text = "headlineMedium", style = Typography.headlineMedium)
            Text(text = "headlineSmall", style = Typography.headlineSmall)
            Text(text = "titleLarge", style = Typography.titleLarge)
            Text(text = "titleMedium", style = Typography.titleMedium)
            Text(text = "titleSmall", style = Typography.titleSmall)
            Text(text = "bodyLarge", style = Typography.bodyLarge)
            Text(text = "bodyMedium", style = Typography.bodyMedium)
            Text(text = "bodySmall", style = Typography.bodySmall)
            Text(text = "labelLarge", style = Typography.labelLarge)
            Text(text = "labelMedium", style = Typography.labelMedium)
            Text(text = "labelSmall", style = Typography.labelSmall)
        }
    }
}
