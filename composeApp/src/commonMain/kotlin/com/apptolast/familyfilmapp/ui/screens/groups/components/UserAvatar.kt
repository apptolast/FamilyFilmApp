package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.components.ProfileAvatar
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.groups_member_avatar
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserAvatar(user: User, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    ProfileAvatar(
        photoUrl = user.photoUrl.trim(),
        contentDescription = stringResource(Res.string.groups_member_avatar, user.displayName),
        modifier = modifier,
        size = size,
        fallbackContent = {
            Text(
                text = user.displayName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewUserAvatar() {
    FamilyFilmAppTheme {
        UserAvatar(
            user = User(
                id = "1",
                email = "alex@example.com",
                language = "en",
                photoUrl = "",
                username = "Alex",
            ),
        )
    }
}
