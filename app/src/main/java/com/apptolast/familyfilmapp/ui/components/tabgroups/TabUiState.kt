package com.apptolast.familyfilmapp.ui.components.tabgroups

data class TabUiState(val selectedGroupPos: Int) {
    constructor() : this(
        selectedGroupPos = 0,
    )
}
