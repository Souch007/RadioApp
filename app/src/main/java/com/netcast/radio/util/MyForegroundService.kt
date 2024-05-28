package com.netcast.radio.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.netcast.radio.R
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MyForegroundService : Service() {

    private val CHANNEL_ID = "netcast_tracking"
    private val CHANNEL_NAME = "netcast_usertracking"
    private val CHANNEL_DESCRIPTION = "user stay detection"
    override fun onCreate() {
        super.onCreate()
        // Service initialization code
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, getNotification())
        // Your service logic here
        callApiOnAppKill( getCurrentDateTimeFormatted(),"")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Return null if the service is not bound
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Call API here
        callApiOnAppKill("", getCurrentDateTimeFormatted())
        stopSelf()
    }

    private fun callApiOnAppKill(inTime:String,outTime:String) {
        // API call logic here, for example using Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val apiService = retrofit.create(AppApis::class.java)
        val call = apiService.notifyAppKilled(
            deviceID,
            getUserCountry(applicationContext) ?: "",
            inTime,
            outTime
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Handle the response
                Log.d("MyForegroundService", "API call successful: ${response.message()}")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
                Log.d("MyForegroundService", "API call failed: ${t.message}")
            }
        })
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun getNotification(): Notification {
        createNotificationChannel(applicationContext)
        val builder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle("Service Running")
            .setContentText("Your service is running in the foreground")
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        return builder.build()
    }

    private fun getUserCountry(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                val locale = Locale("", simCountry)
                return locale.displayCountry
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // Device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    val locale = Locale("", networkCountry)
                    return locale.displayCountry

                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun getCurrentDateTimeFormatted(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above, use DateTimeFormatter
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            currentDateTime.format(formatter)
        } else {
            // For API below 26, use SimpleDateFormat
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            dateFormat.format(Date())
        }
    }
}
