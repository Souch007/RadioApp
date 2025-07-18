package com.netcast.radio

import android.app.Application
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import com.netcast.radio.ui.ui.settings.TimerReceiver
import com.netcast.radio.ui.ui.settings.TimerService

class MyApp : Application() {
    private val timerReceiver = TimerReceiver()
    override fun onCreate() {
        super.onCreate()
//        StrictMode.setThreadPolicy(
//            StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .build()
//        )
//        StrictMode.setVmPolicy(
//            StrictMode.VmPolicy.Builder()
//                .detectAll()
//                .build()
//        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK),RECEIVER_EXPORTED)

            registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED),
                RECEIVER_EXPORTED)
        }else {
            registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK))

            registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED))
        }

    }
}