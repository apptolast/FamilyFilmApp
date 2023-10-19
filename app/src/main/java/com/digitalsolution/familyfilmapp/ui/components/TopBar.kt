package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TabGroups
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun TopBar() {
    TabGroups()
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TopBarPreview() {
    FamilyFilmAppTheme {
        TopBar()
    }
}
