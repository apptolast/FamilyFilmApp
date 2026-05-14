package com.apptolast.familyfilmapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform