package com.netcast.baidutv

import android.app.Application
import android.content.IntentFilter
import com.netcast.baidutv.ui.ui.settings.TimerReceiver
import com.netcast.baidutv.ui.ui.settings.TimerService

class MyApp : Application() {
    private val timerReceiver = TimerReceiver()
    override fun onCreate() {
        super.onCreate()
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK))
        registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED))
    }
}