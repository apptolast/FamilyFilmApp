package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.screens.groups.GroupSummary
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_LIST_CARD
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.groups_member_count
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupListCard(summary: GroupSummary, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag(TT_GROUPS_LIST_CARD),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = summary.group.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = stringResource(Res.string.groups_member_count, summary.group.users.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            AvatarStack(members = summary.members)
        }
    }
}

@Composable
private fun AvatarStack(members: List<User>, modifier: Modifier = Modifier) {
    val visibleMembers = members.take(5)
    val width = if (visibleMembers.isEmpty()) 40.dp else (40 + (visibleMembers.size - 1) * 26).dp
    Box(
        modifier = modifier
            .height(44.dp)
            .width(width),
    ) {
        visibleMembers.forEachIndexed { index, user ->
            key(user.id) {
                UserAvatar(
                    user = user,
                    size = 40.dp,
                    modifier = Modifier.offset(x = (index * 26).dp),
                )
            }
        }
        if (visibleMembers.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
        }
    }
}

@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Composable
private fun PreviewGroupListCard() {
    FamilyFilmAppTheme {
        GroupListCard(
            summary = GroupSummary(
                group = Group(
                    id = "g1",
                    ownerId = "u1",
                    name = "Friday Night",
                    users = listOf("u1", "u2", "u3"),
                    lastUpdated = null,
                ),
                members = listOf(
                    User("u1", "alex@example.com", "en", "", "Alex"),
                    User("u2", "sara@example.com", "en", "", "Sara"),
                    User("u3", "mario@example.com", "en", "", "Mario"),
                ),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
