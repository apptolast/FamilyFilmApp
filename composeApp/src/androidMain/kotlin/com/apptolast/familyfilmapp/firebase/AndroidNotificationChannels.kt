package com.apptolast.familyfilmapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.apptolast.familyfilmapp.R

object AndroidNotificationChannels {
    const val GENERAL_CHANNEL_ID = "familyfilm_general"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            GENERAL_CHANNEL_ID,
            context.getString(R.string.fcm_general_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.fcm_general_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }
}
