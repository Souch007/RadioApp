package com.coderoids.radio.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coderoids.radio.PlayingChannelData
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.google.android.exoplayer2.ExoPlayer
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data

object AppSingelton {
    @kotlin.jvm.JvmField
    public var suggestedRadioList: List<RadioLists>? = null

    //-------------------Player Variables---------------------------------//
    public var exoPlayer: ExoPlayer? = null
    public val _radioSelectedChannel = MutableLiveData<PlayingChannelData>()
    public val radioSelectedChannel: LiveData<PlayingChannelData> = _radioSelectedChannel
    var  _radioSelectedChannelId : String = "";
    var _currenPlayingChannelId : String = "";
    var _currentPlayingChannel = MutableLiveData<PlayingChannelData>()
    val currentPlayingChannel: LiveData<PlayingChannelData> = _currentPlayingChannel

    val _isPlayerFragVisible = MutableLiveData<Boolean>()
    val isPlayerFragVisible : LiveData<Boolean> = _isPlayerFragVisible

    val _isNewStationSelected = MutableLiveData<Boolean>()
    val isNewStationSelected : LiveData<Boolean> = _isNewStationSelected
    //------------------------Favourites-------------------------//
    val _favouritesRadio = MutableLiveData<List<PlayingChannelData>>()
    var favouritesRadioArray =  ArrayList<PlayingChannelData>()
    val favouritesRadio : LiveData<List<PlayingChannelData>> = _favouritesRadio
    var _isFavUpdated = MutableLiveData<Boolean>()
    var currentActivity = "";

    //--------------- Download -------------------------------//
   public var downloadingEpisodeData: Data? = null
    var _progressPublish = MutableLiveData<Int>()
    var progressPublish : LiveData<Int> = _progressPublish

}