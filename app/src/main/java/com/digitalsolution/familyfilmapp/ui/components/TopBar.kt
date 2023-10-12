package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.digitalsolution.familyfilmapp.model.local.GroupData
import com.digitalsolution.familyfilmapp.ui.screens.home.components.TabGroups
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onClickLogOut: () -> Unit,
    title: String,
    groups: List<GroupData>
) {
    if (title.contentEquals("Groups", true)) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.outlineVariant)) {
            TopAppBarWrapper(title = title) {
                onClickLogOut()
            }
            TabGroups(groups = groups, groupScreen = true)
        }
    } else {
        TopAppBarWrapper(title = title) {
            onClickLogOut()
        }
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TopBarPreview() {
    FamilyFilmAppTheme {
        TopBar(
            onClickLogOut = {},
            title = "",
            groups = emptyList()
        )
    }
}
