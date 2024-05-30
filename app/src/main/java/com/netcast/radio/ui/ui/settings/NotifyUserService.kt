package com.netcast.radio.ui.ui.settings

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.netcast.radio.R
import com.netcast.radio.base.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifyUserService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        makeApiCallStart(true)
    }

    override fun onDestroy() {
        Log.d("onDestroy", "onDestroy: ")
        super.onDestroy()
//        makeApiCallStart(false)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("onTaskRemoved123", "onTaskRemoved:12344 ")
//        makeApiCallStart(false)
        val notifyUserWorkRequest = OneTimeWorkRequest.Builder(NotifyUserWorker::class.java).build()
        WorkManager.getInstance(applicationContext).enqueue(notifyUserWorkRequest)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ensure startForeground() is called here if it wasn't already in onCreate
        startForegroundService()
        return START_STICKY
    }
    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "APIServiceChannel"
            val channelName = "API Service Channel"
            val chan =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chan)

            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("API Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.logo)
                .build()

//            startForeground(1, notification)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                startForeground(1, notification)
            }
        }
    }

    private fun makeApiCallStart(istoStart: Boolean) {
        val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val inTime= if (istoStart) getCurrentDateTime() else ""
        val outTime= if (!istoStart) getCurrentDateTime() else ""
        ViewModelProvider.apiViewModel?.notifyAppKilled(
            deviceID,
            detectNetworkCountry(applicationContext) ?: Locale.getDefault().country,
            inTime,
            outTime
        )
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }


    private fun detectNetworkCountry(context: Context): String? {
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            return telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
