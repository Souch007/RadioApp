package com.baidu.netcast.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.baidu.netcast.PlayingChannelData
import com.baidu.netcast.ui.radio.data.temp.RadioLists
import com.google.android.exoplayer2.ExoPlayer
import com.baidu.netcast.ui.radioplayermanager.episodedata.Data

object AppSingelton {

    var currentPlayingPos: Int=0

    @kotlin.jvm.JvmField
    public var suggestedRadioList: List<RadioLists>? = null
    public var selectedChannel: RadioLists? = null
    public var mediaItemList: MutableList<com.google.android.exoplayer2.MediaItem>? = null
    public var classicalList: List<RadioLists>? = null
    public var popList: List<RadioLists>? = null
    public var newsList: List<RadioLists>? = null
    public var publicList: List<RadioLists>? = null
    public var _playingStarted = MutableLiveData<Boolean>()
    //-------------------Player Variables---------------------------------//
    public var exoPlayer: ExoPlayer? = null
    public val _radioSelectedChannel = MutableLiveData<PlayingChannelData>()
    public val radioSelectedChannel: LiveData<PlayingChannelData> = _radioSelectedChannel
    var  _radioSelectedChannelId : String = ""
    var _currenPlayingChannelId : String = ""
    var country=""

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