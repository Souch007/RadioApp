package com.coderoids.radio.ui.radioplayermanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.coderoids.radio.PlayingChannelData
import com.coderoids.radio.base.BaseViewModel
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.data.temp.RadioLists

class RadioPlayerAVM : BaseViewModel() , OnClickListnerRadio {
     var suggestedRadioList: List<RadioLists>? = null
    override fun onRadioClicked(data: RadioLists) {
        TODO("Not yet implemented")
    }
}