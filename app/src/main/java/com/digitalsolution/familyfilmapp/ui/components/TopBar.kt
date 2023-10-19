package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TabGroups
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onClickLogOut: () -> Unit,
) {
    TabGroups()
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TopBarPreview() {
    FamilyFilmAppTheme {
        TopBar(
            onClickLogOut = {},
            title = "",
        )
    }
}
