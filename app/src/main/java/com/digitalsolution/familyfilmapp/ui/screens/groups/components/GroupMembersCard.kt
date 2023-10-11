package com.digitalsolution.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.model.local.MemeberData
import com.digitalsolution.familyfilmapp.ui.theme.bold
import me.saket.swipe.SwipeAction


@Composable
fun GroupMembersCard(
    groupTitle: String,
    members: List<MemeberData>,
    onRemoveMemberClick: (MemeberData) -> Unit,
    onSwipeDelete: (MemeberData) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = groupTitle,
                style = MaterialTheme.typography.titleLarge.bold(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn {
                items(members.toMutableList()) {
                    val delete = SwipeAction(
                        onSwipe = {
                            onSwipeDelete(it)
                        },
                        icon = {

                        },
                        background = Color.Red.copy(alpha = 0.5f),
                        isUndo = true
                    )
                }
            }
        }
    }
}