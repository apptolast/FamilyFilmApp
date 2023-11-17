package com.digitalsolution.familyfilmapp.navigation

import com.compose.type_safe_args.annotation.ComposeDestination
import com.digitalsolution.familyfilmapp.model.local.Movie

@ComposeDestination
interface DetailPage {
    val movie: Movie
}
