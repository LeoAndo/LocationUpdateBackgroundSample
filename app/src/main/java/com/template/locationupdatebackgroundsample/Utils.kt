package com.template.locationupdatebackgroundsample

import android.app.ActivityManager
import android.content.Context

object Utils {
    /**
     * debugコード(本番コードには使用してはダメ).
     * アプリが現在フォアグランド中かどうか判定する.
     */
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        appProcesses.forEach { appProcess ->
            if (appProcess.importance ==
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == context.packageName
            ) {
                return true
            }
        }
        return false
    }
}