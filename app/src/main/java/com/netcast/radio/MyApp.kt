package com.netcast.radio

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.IntentFilter
import android.os.Bundle
import com.netcast.radio.ui.ui.settings.TimerReceiver
import com.netcast.radio.ui.ui.settings.TimerService

class MyApp : Application() {
    private val timerReceiver = TimerReceiver()
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK))
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED))
    }


    fun appInBackground() {
        val startTime = System.currentTimeMillis()
//        sharedPreferences.edit().putLong("startTime", startTime).apply()
    }
    fun calculateTimeSpent() {
//        val startTime = sharedPreferences.getLong("startTime", 0)
//        if (startTime != 0L) {
//            val elapsedTime = System.currentTimeMillis() - startTime
//            val seconds = elapsedTime / 1000
//            Log.d("AppTime", "User used the app for $seconds seconds")
        }
//    }
   /* fun appInForeground() {
        sharedPreferences.edit().remove("startTime").apply()
    }*/
    inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {
            appInBackground()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            appInBackground()
        }
    }
}