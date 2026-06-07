package com.apptolast.familyfilmapp.model.local

data class WatchProviderVisibilityPolicy(
    private val blockedProviderIds: Set<Int> = emptySet(),
    private val blockedNameFragments: Set<String> = emptySet(),
) {
    private val normalizedBlockedNameFragments = blockedNameFragments.map { it.lowercase() }

    fun isVisible(provider: Provider): Boolean {
        if (provider.providerId in blockedProviderIds) return false

        val normalizedName = provider.name.lowercase()
        return normalizedBlockedNameFragments.none { normalizedName.contains(it) }
    }

    companion object {
        val AllowAll = WatchProviderVisibilityPolicy()

        val AppleAppStore = WatchProviderVisibilityPolicy(
            blockedProviderIds = setOf(GOOGLE_PLAY_PROVIDER_ID),
            blockedNameFragments = setOf(GOOGLE_PLAY_NAME_FRAGMENT),
        )

        private const val GOOGLE_PLAY_PROVIDER_ID = 3
        private const val GOOGLE_PLAY_NAME_FRAGMENT = "google play"
    }
}

expect val platformWatchProviderVisibilityPolicy: WatchProviderVisibilityPolicy

fun List<Provider>.visibleTo(
    policy: WatchProviderVisibilityPolicy = platformWatchProviderVisibilityPolicy,
): List<Provider> = filter(policy::isVisible)

fun Media.withVisibleWatchProviders(
    policy: WatchProviderVisibilityPolicy = platformWatchProviderVisibilityPolicy,
): Media = copy(
    streamProviders = streamProviders.visibleTo(policy),
    buyProviders = buyProviders.visibleTo(policy),
    rentProviders = rentProviders.visibleTo(policy),
)
