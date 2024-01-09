package com.netcast.baidutv.ui.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.netcast.baidutv.base.AppSingelton

 class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            TimerService.ACTION_TICK -> {
                val timeLeft = intent.getStringExtra(TimerService.TIME_LEFT_KEY)
                AppSingelton._SleepTimer.value=timeLeft

            }
            TimerService.ACTION_FINISHED -> {
                AppSingelton.exoPlayer?.stop()
                AppSingelton._SleepTimerEnd.value=true
            }
        }
    }
}