package com.netcast.radio.ui.ui.settings.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.databinding.ActivityAlarmSelectedChannelBinding
import com.netcast.radio.request.AppConstants

class AlarmSelectedChannelActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    private lateinit var binding: ActivityAlarmSelectedChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSelectedChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Alarm Channel"
        binding.header.imgBack.setOnClickListener {
            finish()
        }

//        val checkbox = findViewById<CheckBox>(R.id.checkbox_alramchannel)
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        val playingChannelData = retrieveStoredObject(
            AppConstants.SELECTED_ALARM_RADIO, PlayingChannelData::class.java
        )
        playingChannelData?.let {
            binding.tvChannelName.text = it.name
        }
        val alarmChecbox = sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
        if (playingChannelData != null && alarmChecbox) {
            binding.checkboxAlramchannel.isChecked = true
            binding.checkboxAlramchannel.isEnabled = false
        }
        binding.checkboxAlramchannel.setOnCheckedChangeListener { compoundButton, b ->
            if (b) sharedPredEditor.putBoolean(AppConstants.ALARM_CHECKBOX, b).apply()
        }
    }

    fun <T> retrieveStoredObject(prefName: String, baseClass: Class<T>): T? {
        val dataObject: String? = sharedPreferences.getString(prefName, "")
        return Gson().fromJson(dataObject, baseClass)
    }
}