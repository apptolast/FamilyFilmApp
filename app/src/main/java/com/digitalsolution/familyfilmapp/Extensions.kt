package com.digitalsolution.familyfilmapp

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont

fun String.getGoogleFontFamily(): FontFamily {
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    return FontFamily(
        Font(googleFont = GoogleFont(this), fontProvider = provider)
    )
}