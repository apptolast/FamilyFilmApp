package com.apptolast.familyfilmapp.repositories.datasources

import com.russhwolf.settings.Settings

interface RecommendedCardStateDatasource {
    fun getRevealedMediaId(groupId: String): String?
    fun setRevealedMediaId(groupId: String, mediaId: String)
    fun clearRevealedMediaId(groupId: String)
}

class RecommendedCardStateDatasourceImpl(private val settings: Settings) : RecommendedCardStateDatasource {

    override fun getRevealedMediaId(groupId: String): String? = settings.getStringOrNull(keyFor(groupId))

    override fun setRevealedMediaId(groupId: String, mediaId: String) {
        settings.putString(keyFor(groupId), mediaId)
    }

    override fun clearRevealedMediaId(groupId: String) {
        settings.remove(keyFor(groupId))
    }

    private fun keyFor(groupId: String): String = KEY_PREFIX + groupId

    companion object {
        private const val KEY_PREFIX = "ffa_group_recommended_revealed_"
    }
}
