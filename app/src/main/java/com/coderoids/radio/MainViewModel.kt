package com.coderoids.radio

import android.util.Log
import android.util.Log.INFO
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.podcast.PodcastViewModel
import com.coderoids.radio.ui.podcast.adapter.OnClickListenerPodcast
import com.coderoids.radio.ui.podcast.poddata.PodListData
import com.coderoids.radio.ui.radio.RadioViewModel
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.search.SearchViewModel
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.launch
import java.util.logging.Level.INFO

class MainViewModel : ViewModel() , OnClickListnerRadio , OnClickListenerPodcast{

    val remoteDataSource = RemoteDataSource()
    val appRepository = AppRepository(remoteDataSource.buildApi(AppApis::class.java))

    val _isNewStationSelected = MutableLiveData<Boolean>()
    val isNewStationSelected : LiveData<Boolean> = _isNewStationSelected

    val _isStationActive = MutableLiveData<Boolean>()
    val isStationActive : LiveData<Boolean> = _isStationActive

    val _isPlayerFragVisible = MutableLiveData<Boolean>()
    val isPlayerFragVisible : LiveData<Boolean> = _isPlayerFragVisible
    public var exoPlayer: ExoPlayer? = null

    var _currentPlayingChannel = MutableLiveData<PlayingChannelData>()
    val currentPlayingChannel: LiveData<PlayingChannelData> = _currentPlayingChannel

    val _radioSelectedChannel = MutableLiveData<PlayingChannelData>()
    val radioSelectedChannel: LiveData<PlayingChannelData> = _radioSelectedChannel

    val _suggesteStations = MutableLiveData<List<RadioLists>>()
    val suggestedStations : LiveData<List<RadioLists>> = _suggesteStations

    fun getRadioListing(radioViewModel: RadioViewModel) {
        viewModelScope.launch {
            radioViewModel._radioListing.value = appRepository.getRadioListing()
        }
    }

    fun getPodCastListing(podcastViewModel: PodcastViewModel) {
        viewModelScope.launch {
            podcastViewModel._podcastListingMutable.value = appRepository.getPodCastListing()
        }
    }

    fun getLanguages(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._languageListingMutable.value  = appRepository.getLanguages()
        }
    }

    fun getCountires(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._countriesListingMutable.value  = appRepository.getCountries()
        }
    }

    fun getAllGenres(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._genresListinMutable.value  = appRepository.getAllGenres()
        }
    }

    fun getFrequentSearchesTags(searchViewModel : SearchViewModel){
        viewModelScope.launch {
            searchViewModel._frequentSearchesTags.value = appRepository.getFrequentSearchTags()
        }
    }

    fun getSearchQueryResult(searchQuery : String , searchViewModel: SearchViewModel){
        viewModelScope.launch {
            searchViewModel._searchResultsPodcast.value = appRepository.searchPodcasts(searchQuery)
            searchViewModel._searchResultsStations.value = appRepository.searchedStation(searchQuery)
        }
    }



    override fun onRadioClicked(data: RadioLists) {
        var playingChannelData = PlayingChannelData(data.url,data.favicon,data.name,data.id,data.country,"RADIO")
        _radioSelectedChannel.value = playingChannelData
        _isNewStationSelected.value = false
         exoPlayer = null

    }

    override fun onPodCastClicked(data: PodListData) {
        var playingChannelData = PlayingChannelData(data.url,data.image,data.title,data.id,data.author,"PODCAST")
        _radioSelectedChannel.value = playingChannelData
        _isNewStationSelected.value = false
        exoPlayer = null
    }
}