package com.netcast.radio.ui.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.netcast.radio.R
import com.netcast.radio.databinding.FragmentAlarmBinding
import java.util.*


class AlarmFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendar: Calendar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTimer.setOnClickListener {
            val audio = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            audio?.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                70,
                0
            )
            showTimerPickerFragment(it)
        }
        return root
    }

    private fun setAlaram(view: View) {
        when (view.id) {
            R.id.tv_sunday -> {

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
            R.id.tv_monday -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            }
            R.id.tv_tuesday -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)

            }
            R.id.tv_wednesday -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)

            }
            R.id.tv_thursday -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)

            }
            R.id.tv_friday -> {
                calendar.clear(Calendar.FRIDAY)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)

            }
            R.id.tv_saturday -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
        }
        startAlarm(calendar)
/*        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY, Calendar.FRIDAY)
        //calendar.add(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
        //calendar.add(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour())
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute())
        Log.e("Point_1", "Calendar " + calendar.getTime())
        val intent1 = Intent(this@MyService_alarm, MyReceiver_Alarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this@MyService_alarm,
            intent.getIntExtra("Size", 1),
            intent1,
            0
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
        alarmManager!!.setRepeating(
            AlarmManager.RTC,
            calendar.getTimeInMillis(),
            (7 * 24 * 3600 * 1000).toLong(),
            pendingIntent
        )*/
    }

    private fun startAlarm(calendar: Calendar) {

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlramReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )

    }

    private fun showTimerPickerFragment(view: View) {
        calendar = Calendar.getInstance()
        val timePickerFragment = TimePickerFragment(this)
        timePickerFragment.show(requireActivity().supportFragmentManager, "time_picker")
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        startAlarm(calendar)
        binding.tvTimer.text = "$hour : $minute"
        binding.tvTimer2.text = "$hour : $minute"
    }

    private fun checkThisForAlara() {
/*        if (Build.VERSION.SDK_INT >= 23) {
            hour = picker.getHour();
            minute = picker.getMinute();
        } else {
            hour = picker.getCurrentHour();
            minute = picker.getCurrentMinute();
        }
        if (hour > 12) {
            am_pm = Calendar.PM;
            hour = hour - 12;
        } else {
            am_pm = Calendar.AM;
        }
        CheckBox monday = (CheckBox) findViewById(R.id.checkBox);
        CheckBox tuesday = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox wednesday = (CheckBox) findViewById(R.id.checkBox3);
        CheckBox thursday = (CheckBox) findViewById(R.id.checkBox4);
        CheckBox friday = (CheckBox) findViewById(R.id.checkBox5);
        CheckBox saturday = (CheckBox) findViewById(R.id.checkBox6);
        CheckBox sunday = (CheckBox) findViewById(R.id.checkBox7);

        if((monday.isChecked()) || (tuesday.isChecked()) || (wednesday.isChecked()) || (thursday.isChecked()) || (friday.isChecked()) || (saturday.isChecked()) || (sunday.isChecked())) {

            if (monday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.MONDAY);
            }
            if (tuesday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.TUESDAY);
            }
            if (wednesday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.WEDNESDAY);
            }
            if (thursday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.THURSDAY);
            }
            if (friday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.FRIDAY);
            }
            if (saturday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.SATURDAY);
            }
            if (sunday.isChecked()) {
                setAlarm(hour, minute, am_pm, Calendar.SUNDAY);
            }
        }
        else{
            if(((hour*3600000)+(minute*60000)) < (System.currentTimeMillis())){
                setAlarm(hour, minute, am_pm, (Calendar.DAY_OF_WEEK + 1));
            }
            else{
                setAlarm(hour, minute, am_pm, Calendar.DAY_OF_WEEK);
            }
        }

    }
    public void setAlarm(int hr, int min, int ampm, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.AM_PM, ampm);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0 );
        AlarmManager alarmManager= (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        Toast.makeText(this, "The alarm is set", Toast.LENGTH_LONG).show();
        finish();
    }*/


    }
}