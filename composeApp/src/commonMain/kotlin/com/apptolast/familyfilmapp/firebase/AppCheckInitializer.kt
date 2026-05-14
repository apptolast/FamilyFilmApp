package com.apptolast.familyfilmapp.firebase

// GitLive does not expose firebase-app-check; each platform touches the native SDK directly.
expect fun installAppCheckProvider(debug: Boolean)
