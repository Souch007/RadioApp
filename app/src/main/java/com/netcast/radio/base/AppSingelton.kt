package com.netcast.radio.base

import android.media.browse.MediaBrowser.MediaItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.google.android.exoplayer2.ExoPlayer
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

object AppSingelton {

    var currentPlayingPos: Int=0

    @kotlin.jvm.JvmField
    var suggestedRadioList: List<RadioLists>? = null
    var selectedChannel: RadioLists? = null
    var mediaItemList: List<com.google.android.exoplayer2.MediaItem>? = null
    var classicalList: List<RadioLists>? = null
    var popList: List<RadioLists>? = null
    var newsList: List<RadioLists>? = null
    var publicList: List<RadioLists>? = null
    var _playingStarted = MutableLiveData<Boolean>()
    //-------------------Player Variables---------------------------------//
    public var exoPlayer: ExoPlayer? = null
    val _radioSelectedChannel = MutableLiveData<PlayingChannelData>()
    public val radioSelectedChannel: LiveData<PlayingChannelData> = _radioSelectedChannel
    var  _radioSelectedChannelId : String = ""
    var _currenPlayingChannelId : String = ""
    var country=""

    var _currentPlayingChannel = MutableLiveData<PlayingChannelData>()
    val currentPlayingChannel: LiveData<PlayingChannelData> = _currentPlayingChannel
    var _erroPlayingChannel = MutableLiveData("")
    val errorPlayingChannel: LiveData<String> = _erroPlayingChannel

    val _isPlayerFragVisible = MutableLiveData<Boolean>()
    val isPlayerFragVisible : LiveData<Boolean> = _isPlayerFragVisible

    val _isNewStationSelected = MutableLiveData<Boolean>()
    val isNewStationSelected : LiveData<Boolean> = _isNewStationSelected
    //------------------------Favourites-------------------------//
    var favouritesRadioArray =  ArrayList<PlayingChannelData>()
    var recentlyPlayedArray =  ArrayList<PlayingChannelData>()
    var isNewItemAdded =  MutableLiveData<Boolean>()
    var isAlramSet: Boolean=false

    var _isFavUpdated = MutableLiveData<Boolean>()
    var _SleepTimer = MutableLiveData<String>()
    var _SleepTimerEnd = MutableLiveData<Boolean>()
    var currentActivity = ""
    var IsDeleted=MutableLiveData<Boolean>()

    //--------------- Download -------------------------------//
   public var downloadingEpisodeData: Data? = null
    var _progressPublish = MutableLiveData<Int>()
    var progressPublish : LiveData<Int> = _progressPublish

    var _onDownloadCompletion = MutableLiveData<Data>()
    var onDownloadCompletion : LiveData<Data> = _onDownloadCompletion

    var downloadedIds : String = ""
    var currentDownloading : String = ""
//    var currentDownloading = MutableLiveData<String>("")
    //-----------------------------------------------------//

}