package com.netcast.baidutv.ui.ui.settings

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netcast.baidutv.request.AppConstants


class SettingsViewModel : ViewModel() {
    private var _settingDataArray: MutableLiveData<ArrayList<SettingsData>>? = null
    fun getFavs(sharedprefs: SharedPreferences): MutableLiveData<ArrayList<SettingsData>>? {
        if (_settingDataArray == null) {
            _settingDataArray = MutableLiveData()
            addSettings(sharedprefs)
        }
        return _settingDataArray
    }

    private fun addSettings(sharedPredEditor: SharedPreferences) {
        val appmode = sharedPredEditor.getInt("App_Mode", -1)
        val currentMode =
            if (appmode == 1) "Light" else if (appmode == 0) "Dark" else "System Default"
        val settingsList = ArrayList<SettingsData>()
        var settingsData = SettingsData("General", "AppStyle", currentMode, false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData(
            "",
            "Stream only over WIFI",
            "By activating this option, streaming of stations and podcasts will work only with WIFI, so your mobile data will not be consumed",
            true,
            sharedPredEditor.getBoolean("stream_over_wifi", false)
        )
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData(
            "Podcast",
            "Only download episodes over Wi-Fi",
            "Enable this feature to not consume mobile data when downlaoding episodes",
            true,
            sharedPredEditor.getBoolean("download_over_wifi", false)
        )
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData(
            "",
            "Automatically delete completed episodes",
            "Episodes you have finished listening to will be deleted from your device after 48 hours",
            true,
            sharedPredEditor.getBoolean("delete_completed_episode", false)
        )
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData(
            "",
            "Skip slient part of episode",
            "This function hide pauses in podcast",
            true,
            sharedPredEditor.getBoolean(AppConstants.SKIP_SLIENCE, false)
        )
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData(
            "",
            "Autoplay",
            "The next episode from current context will playback automatically",
            true,
            sharedPredEditor.getBoolean(AppConstants.AUTO_PLAY_EPISODES, false)
        )
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData(
            "", "Step back and forward in player", sharedPredEditor.getLong(
                AppConstants.PLAYER_SECS,
                15
            ).toString()+"Secs", false, false
        )
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList
        settingsData = SettingsData("AppInformation", "", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("", "Imprint", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("", "Terms & Conditions", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("", "Privacy Manager", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("", "Privacy", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList
        settingsData = SettingsData("", "FAQ", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList

        settingsData = SettingsData("", "Feedback", "", false, false)
        settingsList.add(settingsData)
        _settingDataArray!!.value = settingsList


    }


}