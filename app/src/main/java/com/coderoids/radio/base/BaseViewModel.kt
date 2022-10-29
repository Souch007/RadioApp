package com.coderoids.radio.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coderoids.radio.PlayingChannelData
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.podcast.poddata.PodListData
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.google.android.exoplayer2.ExoPlayer

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