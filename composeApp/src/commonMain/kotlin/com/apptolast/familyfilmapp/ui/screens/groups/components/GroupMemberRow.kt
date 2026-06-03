package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.screens.groups.MemberMediaStats
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_MEMBER_ROW

@Composable
fun GroupMemberRow(user: User, stats: MemberMediaStats, isOwner: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f),
                shape = MaterialTheme.shapes.medium,
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag(TT_GROUP_DETAIL_MEMBER_ROW),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserAvatar(user = user)

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )

            if (isOwner) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatLabel(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                },
                count = stats.watchedCount,
                modifier = Modifier.widthIn(min = 42.dp),
            )
            StatLabel(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(17.dp),
                    )
                },
                count = stats.toWatchCount,
                modifier = Modifier.widthIn(min = 42.dp),
            )
        }
    }
}

@Composable
private fun StatLabel(icon: @Composable () -> Unit, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        icon()
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGroupMemberRow() {
    FamilyFilmAppTheme {
        GroupMemberRow(
            user = User("u1", "alex@example.com", "en", "", "Alex"),
            stats = MemberMediaStats(watchedCount = 8, toWatchCount = 2),
            isOwner = true,
        )
    }
}

@Preview(name = "Long owner name", showBackground = true, widthDp = 360)
@Composable
private fun PreviewGroupMemberRowLongOwnerName() {
    FamilyFilmAppTheme {
        GroupMemberRow(
            user = User(
                id = "u1",
                email = "very.long.email@example.com",
                language = "en",
                photoUrl = "",
            ),
            stats = MemberMediaStats(watchedCount = 12, toWatchCount = 27),
            isOwner = true,
        )
    }
}
