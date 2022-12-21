package com.netcast.radio.base

import androidx.lifecycle.ViewModel
import com.netcast.radio.PlayingChannelData

abstract class BaseViewModel() : ViewModel() {


    fun removeChannelFromFavourites(value: PlayingChannelData) {
        for(i in AppSingelton.favouritesRadioArray.indices){
            var data = AppSingelton.favouritesRadioArray.get(i);
            if (data.id == value.id){
                AppSingelton.favouritesRadioArray.removeAt(i);
            }
        }
        AppSingelton._isFavUpdated.value = true
    }

    fun addChannelToFavourites(value: PlayingChannelData) {
        var isChannelAlreadyAdded = false
        for(i in AppSingelton.favouritesRadioArray.indices) {
            var data = AppSingelton.favouritesRadioArray.get(i);
            if (data.id == value.id) {
                isChannelAlreadyAdded = true
                break;
            }
        }
        if(!isChannelAlreadyAdded)
            AppSingelton.favouritesRadioArray.add(value)
        AppSingelton._isFavUpdated.value = true
    }

}