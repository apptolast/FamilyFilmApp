package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.ui.theme.bold

@Composable
fun GroupMemberCard(user: User, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
        ),
    ) {
        Text(
            text = user.email,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall.bold(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupMemberCardPreview() {
    FamilyFilmAppTheme {
        GroupMemberCard(
            user = User().copy(email = "Email 1"),
        )
    }
}
