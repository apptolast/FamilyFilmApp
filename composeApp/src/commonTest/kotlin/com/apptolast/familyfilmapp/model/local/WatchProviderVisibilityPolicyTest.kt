package com.apptolast.familyfilmapp.model.local

import kotlin.test.Test
import kotlin.test.assertEquals

class WatchProviderVisibilityPolicyTest {

    @Test
    fun appleAppStorePolicy_hidesGooglePlayByProviderId() {
        val visibleProviders = listOf(
            provider(providerId = 3, name = "Android Movies"),
            provider(providerId = 8, name = "Netflix"),
        ).visibleTo(WatchProviderVisibilityPolicy.AppleAppStore)

        assertEquals(listOf("Netflix"), visibleProviders.map { it.name })
    }

    @Test
    fun appleAppStorePolicy_hidesGooglePlayByName() {
        val visibleProviders = listOf(
            provider(providerId = 999, name = "Google Play Movies"),
            provider(providerId = 119, name = "Amazon Prime Video"),
        ).visibleTo(WatchProviderVisibilityPolicy.AppleAppStore)

        assertEquals(listOf("Amazon Prime Video"), visibleProviders.map { it.name })
    }

    @Test
    fun appleAppStorePolicy_keepsAllowedProviders() {
        val providers = listOf(
            provider(providerId = 2, name = "Apple TV"),
            provider(providerId = 192, name = "YouTube"),
            provider(providerId = 119, name = "Amazon Prime Video"),
            provider(providerId = 332, name = "Fandango At Home"),
        )

        val visibleProviders = providers.visibleTo(WatchProviderVisibilityPolicy.AppleAppStore)

        assertEquals(providers, visibleProviders)
    }

    @Test
    fun allowAllPolicy_keepsGooglePlay() {
        val providers = listOf(
            provider(providerId = 3, name = "Google Play Movies"),
            provider(providerId = 8, name = "Netflix"),
        )

        val visibleProviders = providers.visibleTo(WatchProviderVisibilityPolicy.AllowAll)

        assertEquals(providers, visibleProviders)
    }

    @Test
    fun mediaWithVisibleWatchProviders_filtersEveryAvailabilityBucket() {
        val media = Media(title = "Movie", posterPath = "").copy(
            streamProviders = listOf(
                provider(providerId = 3, name = "Google Play Movies"),
                provider(providerId = 2, name = "Apple TV"),
            ),
            buyProviders = listOf(
                provider(providerId = 999, name = "Google Play"),
                provider(providerId = 119, name = "Amazon Prime Video"),
            ),
            rentProviders = listOf(
                provider(providerId = 3, name = "Google Play"),
                provider(providerId = 192, name = "YouTube"),
            ),
        )

        val visibleMedia = media.withVisibleWatchProviders(WatchProviderVisibilityPolicy.AppleAppStore)

        assertEquals(listOf("Apple TV"), visibleMedia.streamProviders.map { it.name })
        assertEquals(listOf("Amazon Prime Video"), visibleMedia.buyProviders.map { it.name })
        assertEquals(listOf("YouTube"), visibleMedia.rentProviders.map { it.name })
    }

    private fun provider(providerId: Int, name: String): Provider =
        Provider(providerId = providerId, name = name, logoPath = "/$providerId.jpg")
}
