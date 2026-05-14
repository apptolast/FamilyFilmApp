package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stub Groups screen. Lists groups + a quick "create group" affordance.
 * The full UX (tab row, member cards, recommended media carousel,
 * dialogs for create/delete/rename/add-member) is a polish pass post-
 * migration.
 */
@Composable
fun GroupsScreen(viewModel: GroupViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Groups (${state.groups.size})", style = MaterialTheme.typography.titleLarge)
        if (state.isLoading) Text("Loading…")
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(onClick = { viewModel.createGroup("New group") }) { Text("Create group") }

        LazyColumn {
            items(state.groups) { group ->
                Card(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(group.name, style = MaterialTheme.typography.titleMedium)
                        Text("${group.users.size} members", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
