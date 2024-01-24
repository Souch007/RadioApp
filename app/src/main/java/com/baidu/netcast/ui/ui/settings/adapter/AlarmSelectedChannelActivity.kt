package com.baidu.netcast.ui.ui.settings.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.baidu.netcast.BR
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.PlayingChannelData
import com.baidu.netcast.R
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseActivity
import com.baidu.netcast.databinding.ActivityAlarmSelectedChannelBinding
import com.baidu.netcast.databinding.ActivityMainBinding
import com.baidu.netcast.request.AppConstants
import com.baidu.netcast.ui.radio.adapter.AlaramItemsAdapter
import com.baidu.netcast.ui.radio.adapter.OnClickAlarmItem

class AlarmSelectedChannelActivity : BaseActivity<MainViewModel, ActivityMainBinding>(),
    OnClickAlarmItem {

    private lateinit var sharedPredEditor: SharedPreferences.Editor
    private lateinit var binding: ActivityAlarmSelectedChannelBinding
    private lateinit var mainActivityViewModel: MainViewModel
    lateinit var mysharedPreferences: SharedPreferences
    lateinit var adapter: AlaramItemsAdapter
    private var recentlyPlayed: MutableList<PlayingChannelData>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSelectedChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainActivityViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        recentlyPlayed = AppSingelton.recentlyPlayedArray.asReversed()
        mysharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)

        sharedPredEditor = mysharedPreferences.edit()
        val playingChannelData = retrieveStoredObject(
            AppConstants.SELECTED_ALARM_RADIO, PlayingChannelData::class.java
        )
       /* val index = recentlyPlayed?.indexOfFirst { it.id == playingChannelData?.id }
        if (index != -1)
            recentlyPlayed!![index!!].isSelected = true*/
        recentlyPlayed?.forEachIndexed { index, item ->
            item.isSelected = item.id == playingChannelData?.id
        }

        binding.rvAlarm.layoutManager = LinearLayoutManager(this)
        adapter = AlaramItemsAdapter(recentlyPlayed ?: listOf(), this, "")
        binding.rvAlarm.adapter = adapter
        binding.header.tvTitle.text = "Alarm Channel"
        binding.header.imgBack.setOnClickListener {
            finish()
        }


        /*  playingChannelData?.let {
              binding.tvChannelName.text = it.name
          }*/
        /*    val alarmChecbox = mysharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
            if (playingChannelData != null && alarmChecbox) {
                binding.checkboxAlramchannel.isChecked = true
                binding.checkboxAlramchannel.isEnabled = false
            }*/

        /*  binding.checkboxAlramchannel.setOnCheckedChangeListener { compoundButton, b ->
              if (b) sharedPredEditor.putBoolean(AppConstants.ALARM_CHECKBOX, b).apply()
          }*/
    }

    private fun <T> retrieveStoredObject(prefName: String, baseClass: Class<T>): T? {
        val dataObject: String? = mysharedPreferences.getString(prefName, "")
        return Gson().fromJson(dataObject, baseClass)
    }

    override fun onAlramItemClicked(data: PlayingChannelData, pos: Int) {
        recentlyPlayed?.forEachIndexed { index, item ->
            item.isSelected = item.id == data.id
        }

//        recentlyPlayed?.filterNot { it.id == data.id }?.forEach { it.isSelected = false }
        adapter.notifyDataSetChanged()
        storeObjectInSharedPref(
            data, AppConstants.SELECTED_ALARM_RADIO
        )
    }

    override val layoutRes: Int
        get() = R.layout.activity_alarm_selected_channel
    override val bindingVariable: Int
        get() = BR.mainViewModel
    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
}