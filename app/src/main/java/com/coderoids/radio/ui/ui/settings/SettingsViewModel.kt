package com.coderoids.radio.ui.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SettingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var _settingDataArray: MutableLiveData<ArrayList<SettingsData>>? = null
    fun getFavs(): MutableLiveData<ArrayList<SettingsData>>? {
        if (_settingDataArray == null) {
            _settingDataArray = MutableLiveData()
            addSettings()
        }
        return _settingDataArray
    }

    private fun addSettings() {
        val settingsList = ArrayList<SettingsData>()
        var settingsData = SettingsData("General","AppStyle", "System Default",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Stream only over WIFI"
            , "By activating this option, streaming of stations and podcasts will work only with WIFI, so your mobile data will not be consumed",
            true,true)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("Podcast","Only download episodes over Wi-Fi"
            , "Enable this feature to not consume mobile data when downlaoding episodes"
            ,true,true)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Automatically delete completed episodes",
            "Episodes you have finished listening to will be deleted from your device after 48 hours",true,true)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Step back and forward in player", "15 seconds",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("AppInformation","", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Imprint", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Terms", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Privacy Manager", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Privacy", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","FAQ", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("","Feedback", "",false,false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList


    }


}