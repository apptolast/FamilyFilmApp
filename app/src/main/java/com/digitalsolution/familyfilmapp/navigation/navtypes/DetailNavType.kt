package com.digitalsolution.familyfilmapp.navigation.navtypes

import com.compose.type_safe_args.annotation.ComposeDestination
import com.digitalsolution.familyfilmapp.model.local.Movie

@ComposeDestination
interface DetailNavType {
    val movie: Movie
}
