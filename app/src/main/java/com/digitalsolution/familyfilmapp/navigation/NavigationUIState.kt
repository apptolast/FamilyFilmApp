package com.digitalsolution.familyfilmapp.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class NavigationUIState(
    val isBottomBarVisible: MutableState<Boolean>,
    val searchBottomVisible: MutableState<Boolean>,
    val isTopBarVisible: MutableState<Boolean>,
    val titleScreens: MutableState<String>,
) {
    constructor() : this(
        isBottomBarVisible = mutableStateOf(false),
        searchBottomVisible = mutableStateOf(false),
        isTopBarVisible = mutableStateOf(false),
        titleScreens = mutableStateOf(""),
    )


    constructor(
        isBottomBarVisible: Boolean,
        searchBottomVisible: Boolean,
        isTopBarVisible: Boolean,
        titleScreens: String,
    ) : this(
        isBottomBarVisible = mutableStateOf(isBottomBarVisible),
        searchBottomVisible = mutableStateOf(searchBottomVisible),
        isTopBarVisible = mutableStateOf(isTopBarVisible),
        titleScreens = mutableStateOf(titleScreens),
    )
}
