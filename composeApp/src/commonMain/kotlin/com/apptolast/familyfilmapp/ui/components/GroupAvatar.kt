package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.group_avatar
import org.jetbrains.compose.resources.stringResource

/**
 * Circular group image. Falls back to the first two letters of the group name
 * (like [UserAvatar] does with a single letter) when no image is set.
 */
@Composable
fun GroupAvatar(
    group: Group,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    size: Dp = 40.dp,
) {
    ProfileAvatar(
        photoUrl = group.imageUrl.trim(),
        contentDescription = stringResource(Res.string.group_avatar, group.name),
        modifier = modifier,
        size = size,
        fallbackContent = {
            Text(
                text = group.initials(),
                style = textStyle,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        },
    )
}

private fun Group.initials(): String = name.trim().take(2).uppercase().ifBlank { "?" }

@Preview(showBackground = true)
@Composable
private fun PreviewGroupAvatarInitials() {
    FamilyFilmAppTheme {
        GroupAvatar(
            group = Group(id = "1", ownerId = "1", name = "Friday Night", users = emptyList(), lastUpdated = null),
            size = 56.dp,
        )
    }
}
