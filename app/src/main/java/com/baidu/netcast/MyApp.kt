package com.baidu.netcast

import android.app.Application
import android.content.IntentFilter
import com.baidu.netcast.ui.ui.settings.TimerReceiver
import com.baidu.netcast.ui.ui.settings.TimerService

class MyApp : Application() {
    private val timerReceiver = TimerReceiver()
    override fun onCreate() {
        super.onCreate()
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK))
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED))
    }
}