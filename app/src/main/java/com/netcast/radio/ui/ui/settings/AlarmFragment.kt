package com.netcast.radio.ui.ui.settings

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.netcast.radio.MainActivity
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.databinding.FragmentAlarmBinding
import com.netcast.radio.request.AppConstants
import com.netcast.radio.ui.ui.settings.adapter.AlarmSelectedChannelActivity
import java.util.*


class AlarmFragment : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, OnCheckedChangeListener {
    /*private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!*/
    private lateinit var binding: FragmentAlarmBinding

    private lateinit var calendar: Calendar
    private var hour = 0
    private var min = 0
    private var am_pm = ""
    private var audioManager: AudioManager? = null
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    private lateinit var alarmManager: AlarmManager
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text="Alarm Settings"
        binding.header.imgBack.setOnClickListener {
            finish()
        }
/*
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val root: View = binding.root*/
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        hour = sharedPreferences.getInt("hour", 0)
        min = sharedPreferences.getInt("min", 0)
        setTime(hour, min)
        val playingChannelData = retrieveStoredObject(
            AppConstants.SELECTED_ALARM_RADIO,
            PlayingChannelData::class.java
        )
//        val alarmCheckbox=sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
        val alarmCheckbox=sharedPreferences.getBoolean("isAlarmSet", false)
        if (playingChannelData != null && alarmCheckbox)
            binding.tvSelectchannel.text = playingChannelData.name

        binding.tvTimer.setOnClickListener {
            val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            audio?.setStreamVolume(
                AudioManager.STREAM_MUSIC, 70, 0
            )
            showTimerPickerFragment(it)
        }
        binding.swichAlarm.setOnCheckedChangeListener { compoundButton, b ->
            sharedPredEditor.putBoolean("isAlarmSet", b).commit()
            if (b) setAlaram()
            else cancelAlarm()

        }

        binding.tvSelectchannel.setOnClickListener {
          sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
            if (playingChannelData!=null)
                startActivity(Intent(this, AlarmSelectedChannelActivity::class.java))
            else {
                startActivity(Intent(this, MainActivity::class.java))
                AppSingelton.isAlramSet = true
            }
        }
        if (sharedPreferences.getBoolean("isAlarmSet", false)) {
            binding.swichAlarm.isChecked = true
        }
        setSeekBarVolume()
        setcheckBoxListner()

    }
/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val root: View = binding.root
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = requireContext().getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        hour = sharedPreferences.getInt("hour", 0)
        min = sharedPreferences.getInt("min", 0)
        setTime(hour, min)
        val playingChannelData = retrieveStoredObject(
            AppConstants.SELECTED_ALARM_RADIO,
            PlayingChannelData::class.java
        )
        val alarmCheckbox=sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
        if (playingChannelData != null && alarmCheckbox)
            binding.tvSelectchannel.text = playingChannelData.name

        binding.tvTimer.setOnClickListener {
            val audio = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            audio?.setStreamVolume(
                AudioManager.STREAM_MUSIC, 70, 0
            )
            showTimerPickerFragment(it)
        }
        binding.swichAlarm.setOnCheckedChangeListener { compoundButton, b ->
            sharedPredEditor.putBoolean("isAlarmSet", b).commit()
            if (b) setAlaram()
            else cancelAlarm()

        }

        binding.tvSelectchannel.setOnClickListener {
           val alarmcheckbox= sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
            if (playingChannelData!=null)
                startActivity(Intent(requireContext(), AlarmSelectedChannelActivity::class.java))
            else {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                AppSingelton.isAlramSet = true
            }
        }
        if (sharedPreferences.getBoolean("isAlarmSet", false)) {
            binding.swichAlarm.isChecked = true
        }
        setSeekBarVolume()
        setcheckBoxListner()


        return root
    }*/

    private fun setTime(hour: Int, min: Int) {
        var finalhour = hour
        if (hour < 12) {
            am_pm = "AM"
        } else {
            finalhour = hour - 12
            am_pm = "PM"
        }

        binding.tvTimer.text = "$finalhour:$min $am_pm"
        binding.tvTimer2.text = "$finalhour:$min $am_pm"
    }

    private fun setcheckBoxListner() {
        setSelectedDays()
        binding.chkSaturday.setOnCheckedChangeListener(this)
        binding.chkSunday.setOnCheckedChangeListener(this)
        binding.chkMonday.setOnCheckedChangeListener(this)
        binding.chkTuesday.setOnCheckedChangeListener(this)
        binding.chkThursday.setOnCheckedChangeListener(this)
        binding.chkWednesday.setOnCheckedChangeListener(this)
        binding.chkFriday.setOnCheckedChangeListener(this)
    }

    private fun setSelectedDays() {
        val gson = Gson()
        val list = sharedPreferences.readList<String>(gson, "alarm_days")
        if (list.contains("Monday")) binding.chkMonday.isChecked = true
        if (list.contains("Tuesday")) binding.chkTuesday.isChecked = true
        if (list.contains("Wednesday")) binding.chkWednesday.isChecked = true
        if (list.contains("Thursday")) binding.chkThursday.isChecked = true
        if (list.contains("Friday")) binding.chkFriday.isChecked = true
        if (list.contains("Saturday")) binding.chkSaturday.isChecked = true
        if (list.contains("Sunday")) binding.chkSunday.isChecked = true
    }

    private fun setSeekBarVolume() {
        try {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            binding.seekbar.max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)!!
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
            }
        }

        if ((binding.chkMonday.isChecked()) || (binding.chkTuesday.isChecked()) || (binding.chkWednesday.isChecked()) || (binding.chkThursday.isChecked()) || (binding.chkFriday.isChecked()) || (binding.chkSaturday.isChecked()) || (binding.chkSunday.isChecked())) {
            cancelAlarm()
            if (binding.chkMonday.isChecked) {
                setAlarm(Calendar.MONDAY)
            }
            if (binding.chkTuesday.isChecked) {
                setAlarm(Calendar.TUESDAY)
            }
            if (binding.chkWednesday.isChecked) {
                setAlarm(Calendar.WEDNESDAY)
            }
            if (binding.chkThursday.isChecked) {
                setAlarm(Calendar.THURSDAY)
            }
            if (binding.chkFriday.isChecked) {
                setAlarm(Calendar.FRIDAY)
            }
            if (binding.chkSaturday.isChecked) {
                setAlarm(Calendar.SATURDAY)
            }
            if (binding.chkSunday.isChecked) {
                setAlarm(Calendar.SUNDAY)
            }
        } else {
            setDayAlarm()
            /*if (((hour * 3600000) + (min * 60000)) < (System.currentTimeMillis())) {
                setAlarm(Calendar.DAY_OF_WEEK +1)
            } else {
                setAlarm(Calendar.DAY_OF_WEEK)
            }*/

        }
    }

    private fun setAlarm(dayOfWeek: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val now = Calendar.getInstance()
        now[Calendar.SECOND] = 0
        now[Calendar.MILLISECOND] = 0
        if (calendar.before(now)) {    //this condition is used for future reminder that means your reminder not fire for past time
            calendar.add(Calendar.DATE, 7);

        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE //this is needed in Android 12
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }




        val intent = Intent(this, AlramReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, flag
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
        sharedPredEditor.putInt("hour", hour).putInt("min", min).apply()
    }


    @SuppressLint("SuspiciousIndentation")
    private fun cancelAlarm() {
        pendingIntent?.let {
            alarmManager!!.cancel(it)

        }
    }

    private fun showTimerPickerFragment(view: View) {
        calendar = Calendar.getInstance()
        val timePickerFragment = TimePickerFragment(this)

        timePickerFragment.show(supportFragmentManager, "time_picker")
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {

        this.hour = hour
        min = minute

        setAlaram()
        setTime(this.hour, min)
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        val gson = Gson()
        val list = sharedPreferences.readList<String>(gson, "alarm_days") as MutableList
        when (p0?.id) {
            R.id.chk_monday -> {
                if (p0.isChecked) {
                    list.add("Monday")
                } else list.remove("Monday")
            }
            R.id.chk_tuesday -> {
                if (p0.isChecked) list.add("Tuesday")
                else list.remove("Tuesday")
            }
            R.id.chk_wednesday -> {
                if (p0.isChecked) list.add("Wednesday")
                else list.remove("Wednesday")
            }
            R.id.chk_thursday -> {
                if (p0.isChecked) list.add("Thursday")
                else list.remove("Thursday")
            }
            R.id.chk_friday -> {
                if (p0.isChecked) list.add("Friday")
                else list.remove("Friday")
            }
            R.id.chk_saturday -> {
                if (p0.isChecked) list.add("Saturday")
                else list.remove("Saturday")
            }
            R.id.chk_sunday -> {
                if (p0.isChecked) list.add("Sunday")
                else list.remove("Sunday")
            }
        }
        if (binding.swichAlarm.isChecked) setAlaram()
        sharedPreferences.writeList(gson, "alarm_days", list)
    }

    private fun setDayAlarm() {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)

        val now = Calendar.getInstance()
        now[Calendar.SECOND] = 0
        now[Calendar.MILLISECOND] = 0
        if (calendar.before(now)) {    //this condition is used for future reminder that means your reminder not fire for past time
            calendar.add(Calendar.DATE, 7);

        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE //this is needed in Android 12
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }

        val intent = Intent(this, AlramReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, flag
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
            )
        }
        sharedPredEditor.putInt("hour", hour).putInt("min", min).apply()

    }

    fun <T> SharedPreferences.writeList(gson: Gson, key: String, data: List<T>) {
        val json = gson.toJson(data)
        edit { putString(key, json) }
    }

    inline fun <reified T> SharedPreferences.readList(gson: Gson, key: String): List<T> {
        val json = getString(key, "[]") ?: "[]"
        val type = object : TypeToken<List<T>>() {}.type

        return try {
            gson.fromJson(json, type)
        } catch (e: JsonSyntaxException) {
            emptyList()
        }
    }

    fun <T> retrieveStoredObject(prefName: String, baseClass: Class<T>): T? {
        val dataObject: String? = sharedPreferences.getString(prefName, "")
        return Gson().fromJson(dataObject, baseClass)
    }

}