package com.apptolast.familyfilmapp.ui.screens.discover

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaFilter

data class DiscoverUiState(
    val user: User,
    val mediaList: List<Media>,
    val currentMediaIndex: Int,
    val groups: List<Group>,
    val selectedGroupIds: Set<String>,
    val selectedFilter: MediaFilter = MediaFilter.ALL,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(),
        mediaList = emptyList(),
        currentMediaIndex = 0,
        groups = emptyList(),
        selectedGroupIds = emptySet(),
        selectedFilter = MediaFilter.ALL,
        isLoading = false,
        errorMessage = null,
    )

    val currentMedia: Media?
        get() = mediaList.getOrNull(currentMediaIndex)

    val hasMoreMedia: Boolean
        get() = currentMediaIndex < mediaList.size - 1

    val isOutOfMedia: Boolean
        get() = mediaList.isEmpty() || currentMediaIndex >= mediaList.size
}
