package com.template.locationupdatebackgroundsample

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * 常駐サービス.
 */
class MyForegroundService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var myBackgroundLocationService: MyBackgroundLocationService

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: IN $intent")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: IN")
        myBackgroundLocationService =
            MyBackgroundLocationServiceImpl.getInstance(this)
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            id = CHANNEL_ID_BACKGROUND_LOCATION,
            name = "background location channel",
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: IN intent: $intent flags: $flags startId: $startId")
        val notification = notificationManager.createNotification(
            context = this,
            channelId = CHANNEL_ID_BACKGROUND_LOCATION,
            title = "getting Background Location"
        )
        startForeground(NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
        myBackgroundLocationService.startLocationUpdates()

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: IN")
        myBackgroundLocationService.stopLocationUpdates()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MyForegroundService"
    }
}