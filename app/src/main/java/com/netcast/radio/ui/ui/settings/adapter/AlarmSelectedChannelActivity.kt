package com.netcast.radio.ui.ui.settings.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.request.AppConstants

class AlarmSelectedChannelActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_selected_channel)
        val checkbox = findViewById<CheckBox>(R.id.checkbox_alramchannel)
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        val playingChannelData = retrieveStoredObject(
            AppConstants.SELECTED_ALARM_RADIO,
            PlayingChannelData::class.java
        )
        playingChannelData?.let {
            checkbox.text = it.name
        }
       val alarmChecbox= sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
        if (playingChannelData != null && alarmChecbox) {
            checkbox.isChecked = true
            checkbox.isEnabled = false
        }
        checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                sharedPredEditor.putBoolean(AppConstants.ALARM_CHECKBOX, b).apply()
        }
    }

    fun <T> retrieveStoredObject(prefName: String, baseClass: Class<T>): T? {
        val dataObject: String? = sharedPreferences.getString(prefName, "")
        return Gson().fromJson(dataObject, baseClass)
    }
}