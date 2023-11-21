package com.netcast.radio.ui.ui.settings.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.netcast.radio.MainViewModel
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.databinding.ActivityAlarmSelectedChannelBinding
import com.netcast.radio.request.AppConstants
import com.netcast.radio.ui.favourites.adapters.FavouriteAdapter

class AlarmSelectedChannelActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    private lateinit var binding: ActivityAlarmSelectedChannelBinding
    private lateinit var mainActivityViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSelectedChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainActivityViewModel = ViewModelProvider(this)[MainViewModel::class.java]

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

    fun managedRecyclerView() {
        val recentlyPlayedAdapter =
            FavouriteAdapter(
                AppSingelton.recentlyPlayedArray.asReversed(),
                mainActivityViewModel,
                "recently_played"
            )
//        binding.recentlyPlayed.adapter = recentlyPlayedAdapter
    }
}