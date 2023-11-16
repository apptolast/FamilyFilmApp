package com.digitalsolution.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.digitalsolution.familyfilmapp.ui.theme.bold

@Composable
fun GroupCard(group: Group, onRemoveMemberClick: (Group) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.scrim,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // TODO: Does groups really have an image?
//            AsyncImage(
//                model = groupInfo.,
//                contentDescription = "Member image",
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(CircleShape)
//                    .background(
//                        MaterialTheme.colorScheme.primaryContainer
//                    ),
//                contentScale = ContentScale.Crop
//            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleSmall.bold(),
                    color = Color.White,
                )
            }
            IconButton(onClick = { onRemoveMemberClick(group) }) {
                Icon(
                    imageVector = Icons.Filled.RemoveCircleOutline,
                    contentDescription = "Delete Member",
                    tint = Color.White,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun GroupCardPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            group = Group(
                1,
                "Group Test",
                arrayListOf(Movie()),
                arrayListOf(
                    Movie(),
                ),
            ),
            onRemoveMemberClick = { _ -> },
        )
    }
}
