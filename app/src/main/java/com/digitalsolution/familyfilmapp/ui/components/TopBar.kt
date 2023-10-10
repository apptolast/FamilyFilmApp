package com.digitalsolution.familyfilmapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.digitalsolution.familyfilmapp.model.local.GroupData
import com.digitalsolution.familyfilmapp.ui.screens.home.components.TabGroups
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onClickLogOut: () -> Unit,
    title: String,
    groups: List<GroupData>
) {
    val customTopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.outlineVariant
    )
    if (title.contentEquals("Groups", true)) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.outlineVariant)) {
            TopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Settings icon",
                        modifier = Modifier.clickable {
                            onClickLogOut()
                        }
                    )
                },
                colors = customTopAppBarColors
            )
            TabGroups(groups = groups, viewModel = null, groupScreen = true)
        }
    } else {
        TopAppBar(
            title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
            actions = {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = "Settings icon",
                    modifier = Modifier.clickable {
                        onClickLogOut()
                    }
                )
            },
            colors = customTopAppBarColors
        )
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
