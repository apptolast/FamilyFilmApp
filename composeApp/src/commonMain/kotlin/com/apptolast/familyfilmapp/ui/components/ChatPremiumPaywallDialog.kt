package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.purchases.PeriodUnit
import com.apptolast.familyfilmapp.purchases.SubscriptionPricing
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.chat_paywall_bullet_cancel
import familyfilmkmp.composeapp.generated.resources.chat_paywall_bullet_priority
import familyfilmkmp.composeapp.generated.resources.chat_paywall_bullet_questions
import familyfilmkmp.composeapp.generated.resources.chat_paywall_cta
import familyfilmkmp.composeapp.generated.resources.chat_paywall_dismiss
import familyfilmkmp.composeapp.generated.resources.chat_paywall_price_loading
import familyfilmkmp.composeapp.generated.resources.chat_paywall_price_per_period
import familyfilmkmp.composeapp.generated.resources.chat_paywall_renewal_disclosure
import familyfilmkmp.composeapp.generated.resources.chat_paywall_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_paywall_title
import familyfilmkmp.composeapp.generated.resources.chat_premium_upsell_title
import familyfilmkmp.composeapp.generated.resources.subscription_period_day
import familyfilmkmp.composeapp.generated.resources.subscription_period_month
import familyfilmkmp.composeapp.generated.resources.subscription_period_week
import familyfilmkmp.composeapp.generated.resources.subscription_period_year
import familyfilmkmp.composeapp.generated.resources.subscription_privacy_policy
import familyfilmkmp.composeapp.generated.resources.subscription_terms_of_use
import org.jetbrains.compose.resources.stringResource

/**
 * Compliant Chat Premium paywall (App Store Guideline 3.1.2(c)): shows the subscription
 * title, the billed amount as the most prominent pricing element, the auto-renewal terms,
 * and functional links to the Privacy Policy and the Terms of Use (Apple's standard EULA).
 * Shared by the Chat screen and the Profile screen so the purchase flow is identical
 * wherever Chat Premium is offered.
 */
@Composable
fun ChatPremiumPaywallDialog(
    pricing: SubscriptionPricing?,
    isPurchasing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = { if (!isPurchasing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(Res.string.chat_paywall_title))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.chat_paywall_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                )
                PaywallBullet(stringResource(Res.string.chat_paywall_bullet_questions))
                PaywallBullet(stringResource(Res.string.chat_paywall_bullet_priority))
                PaywallBullet(stringResource(Res.string.chat_paywall_bullet_cancel))

                // The billed amount is the most conspicuous pricing element (Guideline 3.1.2(c)).
                PaywallPriceBlock(pricing)

                // Auto-renewal disclosure, kept subordinate to the billed amount above.
                Text(
                    text = stringResource(Res.string.chat_paywall_renewal_disclosure),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Functional links required for auto-renewable subscriptions.
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        text = stringResource(Res.string.subscription_privacy_policy),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { uriHandler.openUri(PRIVACY_POLICY_URL) },
                    )
                    Text(
                        text = stringResource(Res.string.subscription_terms_of_use),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { uriHandler.openUri(TERMS_OF_USE_URL) },
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isPurchasing,
                modifier = Modifier.heightIn(min = 44.dp),
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.chat_paywall_cta))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isPurchasing) {
                Text(stringResource(Res.string.chat_paywall_dismiss))
            }
        },
    )
}

@Composable
private fun PaywallPriceBlock(pricing: SubscriptionPricing?) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(Res.string.chat_premium_upsell_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (pricing != null) {
                Text(
                    text = stringResource(
                        Res.string.chat_paywall_price_per_period,
                        pricing.priceString,
                        periodLabel(pricing.periodUnit, pricing.periodCount),
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.chat_paywall_price_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallBullet(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun periodLabel(unit: PeriodUnit, count: Int): String {
    val base = when (unit) {
        PeriodUnit.DAY -> stringResource(Res.string.subscription_period_day)
        PeriodUnit.WEEK -> stringResource(Res.string.subscription_period_week)
        PeriodUnit.MONTH -> stringResource(Res.string.subscription_period_month)
        PeriodUnit.YEAR -> stringResource(Res.string.subscription_period_year)
        PeriodUnit.UNKNOWN -> stringResource(Res.string.subscription_period_month)
    }
    return if (count <= 1) base else "$count $base"
}

// Privacy policy is hosted by AppToLast; Terms of Use points to Apple's standard EULA.
private const val PRIVACY_POLICY_URL = "https://apptolast.github.io/FamilyFilmApp/privacy-policy"
private const val TERMS_OF_USE_URL = "https://www.apple.com/legal/internet-services/itunes/dev/stdeula/"
