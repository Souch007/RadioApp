package com.baidu.netcast.ui.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.baidu.netcast.R
import java.util.*

class AlaramActivity : AppCompatActivity() {
    lateinit var btnSetAlarm: Button
    lateinit var timePicker: TimePicker
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alaram)
        title = "KotlinApp"
        timePicker = findViewById(R.id.timePicker)
        btnSetAlarm = findViewById(R.id.buttonAlarm)
        btnSetAlarm.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            if (Build.VERSION.SDK_INT >= 23) {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour,
                    timePicker.minute,
                    0
                )
            } else {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.currentHour,
                    timePicker.currentMinute, 0
                )
            }
            setAlarm(calendar.timeInMillis)
        }
    }
    private fun setAlarm(timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(this, "Alarm is done set", Toast.LENGTH_SHORT).show()
    }
    private class MyAlarm : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            Toast.makeText(context, "DOne", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(MyAlarm(), IntentFilter(TimerService.ACTION_TICK))
    }
}