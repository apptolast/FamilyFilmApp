package com.digitalsolution.familyfilmapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.getGoogleFontFamily

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontSize = 26.sp,
        fontFamily = "Holtwood One SC".getGoogleFontFamily(),
        fontWeight = FontWeight(200),
        color = Color(0xFFFFFFFF),
        textAlign = TextAlign.Center
    ),

    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontFamily = "Homenaje".getGoogleFontFamily(),
        fontWeight = FontWeight(400),
        color = Color(0xFFFFFFFF),
        textAlign = TextAlign.Center
    )

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
