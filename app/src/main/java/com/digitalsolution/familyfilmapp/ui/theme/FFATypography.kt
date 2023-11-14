package com.digitalsolution.familyfilmapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.R

val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.W400),
    Font(R.font.roboto_regular, FontWeight.W500),
)

val HolwoodOne = FontFamily(
    Font(R.font.holtwood_one_sc, FontWeight.W400),
)

val FFATypo = FFATypography()

data class FFATypography(
    val title: FFATitle = FFATitle(),
    val display: FFADisplay = FFADisplay(),
    val headline: FFAHeadline = FFAHeadline(),
    val body: FFABody = FFABody(),
)

data class FFATitle(
    val titleL: TextStyle = TextStyle(
        fontFamily = HolwoodOne,
        fontWeight = FontWeight.W500,
        fontSize = 26.sp,
    ),
)

data class FFADisplay(
    val displayL: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 48.sp,
    ),
    val displayM: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 40.sp,
    ),
    val displayS: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 32.sp,
    ),
)

data class FFAHeadline(
    val headlineXXL: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 28.sp,
    ),
    val headlineXL: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 26.sp,
    ),
    val headlineL: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 24.sp,
    ),
    val headlineM: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
    ),
    val headlineS: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 18.sp,
    ),
    val headlineXS: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
    ),
    val headlineXXS: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
    ),
)

data class FFABody(
    val bodyXL: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
    ),
    val bodyL: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
    ),
    val bodyM: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
    ),
    val bodyS: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
    ),
    val bodyXS: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        fontSize = 11.sp,
    ),
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewFFATypography() {
    FFATheme {
        Column {
            Text(text = "TitleL", style = FFATheme.typography.title.titleL)
            Text(text = "DisplayL", style = FFATheme.typography.display.displayL)
            Text(text = "DisplayM", style = FFATheme.typography.display.displayM)
            Text(text = "DisplayS", style = FFATheme.typography.display.displayS)
            Text(text = "HeadlineXXL", style = FFATheme.typography.headline.headlineXXL)
            Text(text = "HeadlineXL", style = FFATheme.typography.headline.headlineXL)
            Text(text = "HeadlineL", style = FFATheme.typography.headline.headlineL)
            Text(text = "HeadlineM", style = FFATheme.typography.headline.headlineM)
            Text(text = "HeadlineS", style = FFATheme.typography.headline.headlineS)
            Text(text = "HeadlineXS", style = FFATheme.typography.headline.headlineXS)
            Text(text = "HeadlineXXS", style = FFATheme.typography.headline.headlineXXS)
            Text(text = "BodyXL", style = FFATheme.typography.body.bodyXL)
            Text(text = "BodyL", style = FFATheme.typography.body.bodyL)
            Text(text = "BodyM", style = FFATheme.typography.body.bodyM)
            Text(text = "BodyS", style = FFATheme.typography.body.bodyS)
            Text(text = "BodyXS", style = FFATheme.typography.body.bodyXS)
        }
    }
}
