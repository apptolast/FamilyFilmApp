package com.digitalsolution.familyfilmapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.R

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

val secondarySemiBoldHeadLineM = TextStyle(
    fontFamily = FontFamily(Font(R.font.opensans_condensed_semibold)),
    fontSize = 23.sp,
)

val secondaryRegularBodyL = TextStyle(
    fontFamily = FontFamily(Font(R.font.open_sans_regular)),
    fontSize = 16.sp,
)

val secondarySemiBoldHeadLineS = TextStyle(
    fontFamily = FontFamily(Font(R.font.opensans_condensed_semibold)),
    fontSize = 19.sp,
)

fun TextStyle.bold(): TextStyle = this.copy(fontWeight = FontWeight.Bold)
