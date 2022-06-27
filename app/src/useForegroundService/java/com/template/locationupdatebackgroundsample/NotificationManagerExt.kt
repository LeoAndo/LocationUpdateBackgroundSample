package com.template.locationupdatebackgroundsample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

const val NOTIFICATION_ID_FOREGROUND_SERVICE = 101
const val CHANNEL_ID_BACKGROUND_LOCATION: String =
    BuildConfig.APPLICATION_ID + ".channel_background_location"

fun NotificationManager.createNotificationChannel(id: String, name: CharSequence, importance: Int) {
    val notificationChannel = NotificationChannel(
        id,  // Unique ID in application.
        name,// Channel name that the user will see in the "Settings" application.
        importance// Channel importance
    )
    // Register the channel on the terminal and allow it to be seen in "Settings".
    createNotificationChannel(notificationChannel)
}

fun NotificationManager.createNotification(
    context: Context,
    channelId: String,
    title: String
): Notification {
    return Notification.Builder(context, channelId)
        .setContentTitle(title)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(false)
        .build()
}