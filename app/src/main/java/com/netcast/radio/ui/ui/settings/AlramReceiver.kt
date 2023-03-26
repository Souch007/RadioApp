package com.netcast.radio.ui.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlramReceiver :BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent?) {
        val notificationUtils = NotificationUtils(p0)
        val notification = notificationUtils.getNotificationBuilder().build()
        notificationUtils.getManager().notify(150, notification)

    }
}