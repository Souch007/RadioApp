package com.netcast.radio

import android.app.Application
import android.content.IntentFilter
import com.netcast.radio.ui.ui.settings.TimerReceiver
import com.netcast.radio.ui.ui.settings.TimerService

class MyApp : Application() {
    private val timerReceiver = TimerReceiver()
    override fun onCreate() {
        super.onCreate()
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK))
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED))
    }
}