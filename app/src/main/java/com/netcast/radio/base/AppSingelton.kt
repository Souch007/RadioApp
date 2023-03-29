package com.netcast.radio.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.google.android.exoplayer2.ExoPlayer
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

object AppSingelton {
    @kotlin.jvm.JvmField
    public var suggestedRadioList: List<RadioLists>? = null
    public var _playingStarted = MutableLiveData<Boolean>()
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
    var favouritesRadioArray =  ArrayList<PlayingChannelData>()
    var recentlyPlayedArray =  ArrayList<PlayingChannelData>()
    var isNewItemAdded =  MutableLiveData<Boolean>()

    var _isFavUpdated = MutableLiveData<Boolean>()
    var _SleepTimer = MutableLiveData<String>()
    var currentActivity = "";

    //--------------- Download -------------------------------//
   public var downloadingEpisodeData: Data? = null
    var _progressPublish = MutableLiveData<Int>()
    var progressPublish : LiveData<Int> = _progressPublish

    var _onDownloadCompletion = MutableLiveData<Data>()
    var onDownloadCompletion : LiveData<Data> = _onDownloadCompletion

    var downloadedIds : String = ""
    var currentDownloading : String = ""
    //-----------------------------------------------------//

}