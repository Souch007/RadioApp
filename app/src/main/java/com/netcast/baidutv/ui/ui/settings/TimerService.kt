package com.netcast.baidutv.ui.ui.settings

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import java.util.concurrent.TimeUnit

class TimerService : Service() {

    private val finishedIntent = Intent(ACTION_FINISHED)

    private val tickIntent = Intent(ACTION_TICK)

    private lateinit var timer: CountDownTimer

    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = createTimer(intent?.getLongExtra("time",0)!!)
        timer.start()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timer.cancel()
    }




    private fun createTimer(COUNTDOWN_LENGTH:Long): CountDownTimer =
        object :    CountDownTimer(COUNTDOWN_LENGTH * 1000 * 60, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time=String.format(
                    "%d:%d ",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )

                tickIntent.putExtra(TIME_LEFT_KEY, time)
                sendBroadcast(tickIntent)


            }

            override fun onFinish() {
                sendBroadcast(finishedIntent)
                stopSelf()
            }
        }
    companion object {

        const val ACTION_FINISHED: String = "com.netcast.baidutv.ACTION_FINISHED"

        const val ACTION_TICK: String = "com.netcast.baidutv.ACTION_TICK"

        const val TIME_LEFT_KEY: String = "timeLeft"

        private val COUNTDOWN_INTERVAL = TimeUnit.SECONDS.toMillis(1)

//        private val COUNTDOWN_LENGTH = TimeUnit.MINUTES.toMillis(5)
    }
}