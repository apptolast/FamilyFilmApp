package com.apptolast.familyfilmapp.navigation.navtypes

import com.apptolast.familyfilmapp.model.local.Movie
import com.compose.type_safe_args.annotation.ComposeDestination

@ComposeDestination
interface DetailNavType {
    val movie: Movie
}
