package com.template.locationupdatebackgroundsample

import android.app.Application
import android.app.NotificationManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // アプリで使う通知チャンネルはここで全て作成する。
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.createNotificationChannel(
//            id = CHANNEL_ID_BACKGROUND_LOCATION,
//            name = "background location channel",
//            importance = NotificationManager.IMPORTANCE_DEFAULT
//        )
    }
}