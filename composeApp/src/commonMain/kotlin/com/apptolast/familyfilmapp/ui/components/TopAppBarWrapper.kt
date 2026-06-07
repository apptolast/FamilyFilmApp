package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWrapper(title: String, onClickLogOut: () -> Unit = {}) {
    val customTopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.outlineVariant,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        actions = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Settings icon",
                modifier = Modifier.clickable {
                    onClickLogOut()
                },
            )
        },
        colors = customTopAppBarColors,
    )
}

// Outlines the glyphs (border on the text, not on the box): stroke layer behind, fill on top.
// Stroke is centered on the glyph path, so 1.dp shows ~0.5.dp outside — double it for thicker.
@Composable
fun OutlinedText(
    text: String,
    style: TextStyle,
    fillColor: Color,
    modifier: Modifier = Modifier,
    outlineColor: Color = Color.White,
    outlineWidth: Dp = 1.dp,
) {
    val outlinePx = with(LocalDensity.current) { outlineWidth.toPx() }
    Box(modifier) {
        Text(
            text = text,
            style = style.copy(
                color = outlineColor,
                drawStyle = Stroke(width = outlinePx, join = StrokeJoin.Round),
            ),
            modifier = Modifier.clearAndSetSemantics {},
        )
        Text(
            text = text,
            style = style.copy(color = fillColor),
        )
    }
}

@Preview
@Composable
private fun PreviewTopAppBarWrapper() {
    FamilyFilmAppTheme {
        TopAppBarWrapper("Title")
    }
}
