package com.apptolast.familyfilmapp.ui.screens.discover

import com.apptolast.familyfilmapp.model.local.Media

fun interface MediaShuffler {
    fun shuffle(media: List<Media>): List<Media>
}

class RandomMediaShuffler : MediaShuffler {
    override fun shuffle(media: List<Media>): List<Media> = media.shuffled()
}
