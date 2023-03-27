package com.netcast.radio.ui.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.netcast.radio.databinding.FragmentAlarmBinding
import java.util.*


class AlarmFragment : Fragment(), TimePickerDialog.OnTimeSetListener, OnCheckedChangeListener {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendar: Calendar
    private var hour = 0
    private var min = 0
    private var am_pm = 0
    private var audioManager: AudioManager? = null
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
//        val dateNow = Calendar.getInstance().time
        hour = sharedPreferences.getInt("hour",0)
        min = sharedPreferences.getInt("min",0)
        setTime(hour, min)
        binding.tvTimer.setOnClickListener {
            val audio = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            audio?.setStreamVolume(
                AudioManager.STREAM_MUSIC, 70, 0
            )
            showTimerPickerFragment(it)
            setcheckBoxListner()
        }
        setSeekBarVolume()
        binding.swichAlarm.setOnCheckedChangeListener { compoundButton, b ->
            if (b) setAlaram()
            else cancelAlarm()

        }
        return root
    }

    private fun setTime(hour: Int, min: Int) {
        binding.tvTimer.text = "$hour:$min"
        binding.tvTimer2.text = "$hour:$min"
    }

    private fun setcheckBoxListner() {
        binding.chkSaturday.setOnCheckedChangeListener(this)
        binding.chkSunday.setOnCheckedChangeListener(this)
        binding.chkMonday.setOnCheckedChangeListener(this)
        binding.chkTuesday.setOnCheckedChangeListener(this)
        binding.chkThursday.setOnCheckedChangeListener(this)
        binding.chkFriday.setOnCheckedChangeListener(this)
    }

    private fun setSeekBarVolume() {
        try {
            audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            binding.seekbar.setMax(audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)!!)
            binding.seekbar.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC);
            binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAlaram() {
        if ((binding.chkMonday.isChecked()) || (binding.chkTuesday.isChecked()) || (binding.chkWednesday.isChecked()) || (binding.chkThursday.isChecked()) || (binding.chkFriday.isChecked()) || (binding.chkSaturday.isChecked()) || (binding.chkSunday.isChecked())) {

            if (binding.chkMonday.isChecked()) {
                setAlarm(Calendar.MONDAY)
            }
            if (binding.chkTuesday.isChecked()) {
                setAlarm(Calendar.TUESDAY)
            }
            if (binding.chkWednesday.isChecked()) {
                setAlarm(Calendar.WEDNESDAY)
            }
            if (binding.chkThursday.isChecked()) {
                setAlarm(Calendar.THURSDAY)
            }
            if (binding.chkFriday.isChecked()) {
                setAlarm(Calendar.FRIDAY)
            }
            if (binding.chkSaturday.isChecked()) {
                setAlarm(Calendar.SATURDAY)
            }
            if (binding.chkSunday.isChecked()) {
                setAlarm(Calendar.SUNDAY)
            }
        } else {
            if (((hour * 3600000) + (min * 60000)) < (System.currentTimeMillis())) {
                setAlarm(Calendar.DAY_OF_WEEK + 1)
            } else {
                setAlarm(Calendar.DAY_OF_WEEK)
            }

        }
    }

    private fun setAlarm(dayOfWeek: Int) {
        if (hour > 12) {
            am_pm = Calendar.PM
            hour = hour - 12
        } else {
            am_pm = Calendar.AM
        }


        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.AM_PM, am_pm)
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE //this is needed in Android 12
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlramReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent, flag
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
     sharedPredEditor.putInt("hour",hour).putInt("min",min).apply()
    }

    private fun cancelAlarm() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val myIntent = Intent(
            requireContext(), AlramReceiver::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager!!.cancel(pendingIntent)
    }

    private fun showTimerPickerFragment(view: View) {
        calendar = Calendar.getInstance()
        val timePickerFragment = TimePickerFragment(this)
        timePickerFragment.show(requireActivity().supportFragmentManager, "time_picker")
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        this.hour = hour
        min = minute
        setAlaram()
        setTime(hour, min)
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (binding.swichAlarm.isChecked) setAlaram()
    }

}