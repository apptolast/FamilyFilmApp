package com.digitalsolution.familyfilmapp.navigation

import com.compose.type_safe_args.annotation.ComposeDestination

@ComposeDestination
abstract class DetailPage {
    abstract val image: String
    abstract val title: String
    abstract val date: String
    abstract val voteAverage: Float
    abstract val isAdult: Boolean
    abstract val synopsis: String
}
