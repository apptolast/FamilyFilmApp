package com.digitalsolution.familyfilmapp.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class NavigationUIState(
    val isBottomBarVisible: MutableState<Boolean>,
    val searchBottomVisible: MutableState<Boolean>,
    val isTopBarVisible: MutableState<Boolean>,
    val titleScreens: MutableState<Int?>,
) {
    constructor() : this(
        isBottomBarVisible = mutableStateOf(false),
        searchBottomVisible = mutableStateOf(false),
        isTopBarVisible = mutableStateOf(false),
        titleScreens = mutableStateOf(null),
    )

    constructor(
        isBottomBarVisible: Boolean,
        searchBottomVisible: Boolean,
        isTopBarVisible: Boolean,
        @StringRes titleScreens: Int?,
    ) : this(
        isBottomBarVisible = mutableStateOf(isBottomBarVisible),
        searchBottomVisible = mutableStateOf(searchBottomVisible),
        isTopBarVisible = mutableStateOf(isTopBarVisible),
        titleScreens = mutableStateOf(titleScreens),
    )
}
