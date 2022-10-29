package com.coderoids.radio

import android.content.SharedPreferences
import android.util.Log
import android.util.Log.INFO
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseViewModel
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.favourites.adapters.OnFavouriteClickListener
import com.coderoids.radio.ui.podcast.PodcastViewModel
import com.coderoids.radio.ui.podcast.adapter.OnClickListenerPodcast
import com.coderoids.radio.ui.podcast.poddata.PodListData
import com.coderoids.radio.ui.radio.RadioViewModel
import com.coderoids.radio.ui.radio.adapter.OnClickGeneresListener
import com.coderoids.radio.ui.radio.adapter.OnClickListenerCountires
import com.coderoids.radio.ui.radio.adapter.OnClickListenerLanguages
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.countries.Data
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.search.SearchViewModel
import com.coderoids.radio.ui.search.adapters.OnSearchTagListener
import com.coderoids.radio.ui.search.adapters.PodSearchOnClickListener
import com.coderoids.radio.ui.search.adapters.StationSearchListener
import com.coderoids.radio.ui.seeall.adapter.OnClickListenerSeeAll
import com.coderoids.radio.ui.seeall.adapter.OnClickListerPODSeeAll
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.launch
import java.util.logging.Level.INFO

class MainViewModel : BaseViewModel() , OnClickListnerRadio , OnClickListenerPodcast , OnFavouriteClickListener ,
    OnClickListenerSeeAll , OnClickListerPODSeeAll , OnClickListenerLanguages,
    OnClickListenerCountires, OnClickGeneresListener  , OnSearchTagListener ,
    PodSearchOnClickListener , StationSearchListener {
    val remoteDataSource = RemoteDataSource()
    val appRepository = AppRepository(remoteDataSource.buildApi(AppApis::class.java))

    val _state = MutableLiveData<Boolean>()
    val state : LiveData<Boolean> get() = _state

    val _isStationActive = MutableLiveData<Boolean>()
    val isStationActive : LiveData<Boolean> = _isStationActive

    var _currentPlayingChannel = MutableLiveData<PlayingChannelData>()
    val currentPlayingChannel: LiveData<PlayingChannelData> = _currentPlayingChannel

    val _radioSelectedChannel = MutableLiveData<PlayingChannelData>()
    val radioSelectedChannel: LiveData<PlayingChannelData> = _radioSelectedChannel

    val _suggesteStations = MutableLiveData<List<RadioLists>>()
    val suggestedStations : LiveData<List<RadioLists>> = _suggesteStations

    val _favouritesRadio = MutableLiveData<List<PlayingChannelData>>()
    var favouritesRadioArray =  ArrayList<PlayingChannelData>()
    val favouritesRadio : LiveData<List<PlayingChannelData>> = _favouritesRadio

    //---------------------------------------------------------------------//
    var _selectedSeeAllListRadio = MutableLiveData<List<RadioLists>>()
    val selectedSeeAllListRadio: LiveData<List<RadioLists>> = _selectedSeeAllListRadio
    val _selectedSeeAllPodcasts = MutableLiveData<List<PodListData>>()
    val selectedSeeAllPodcasts: LiveData<List<PodListData>> = _selectedSeeAllPodcasts
    val _radioSeeAllSelected = MutableLiveData<String>()
    //------------------------------------------------------------------//
    val _queriedSearched = MutableLiveData<String>()
    var valueTypeFrag : String = ""
    var currentFragmentId : String = "Radio"
    val navigateToPodcast = MutableLiveData<Boolean>()

    //----------------------------------//

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
        var playingChannelData = PlayingChannelData(data.url,data.favicon,data.name,data.id,"",data.country,"RADIO")
        AppSingelton._radioSelectedChannel.value = playingChannelData
        if(AppSingelton._currenPlayingChannelId.matches(data.id.toRegex()))
            AppSingelton._isNewStationSelected.value = false
        else {
            AppSingelton._isNewStationSelected.value = true
            AppSingelton.exoPlayer = null
        }

    }

    override fun onPodCastClicked(data: PodListData) {
        var playingChannelData = PlayingChannelData(data.url,data.image,data.title,data.id,data._id.toString(),data.author,"PODCAST")
        AppSingelton._radioSelectedChannel.value = playingChannelData
        if(AppSingelton._currenPlayingChannelId.matches(data.id.toRegex()))
            AppSingelton._isNewStationSelected.value = false
        else {
            AppSingelton._isNewStationSelected.value = true
            AppSingelton.exoPlayer = null
        }
    }


    override fun onFavChannelClicked(playingChannelData: PlayingChannelData) {
        AppSingelton._radioSelectedChannel.value = playingChannelData
        AppSingelton._isNewStationSelected.value = false
        AppSingelton.exoPlayer = null
    }

    override fun onSeeAllClick(data: RadioLists) {
        onRadioClicked(data)
    }

    override fun onPodClicked(data: PodListData) {
        onPodCastClicked(data)
    }

    override fun OnCountrlySelected(data: Data) {
        _queriedSearched.value = data.name
    }

    override fun OnClickGenres(data: com.coderoids.radio.ui.radio.genres.Data) {
        _queriedSearched.value = data.name

    }

    override fun onLanguageClicked(data: com.coderoids.radio.ui.radio.lanuages.Data) {
        _queriedSearched.value = data.name
    }

    override fun onSearchTagClicked(data: com.coderoids.radio.ui.search.frequentsearch.Data) {
        _queriedSearched.value = data.q
    }

    override fun onPodCastSearchedListener(data: com.coderoids.radio.ui.search.searchedpodresponce.Data) {
        val playingChannelData = PlayingChannelData(data.url,data.image,data.title,data.id,data._id.toString(),data.author,"PODCAST")
        AppSingelton._radioSelectedChannel.value = playingChannelData
        AppSingelton._isNewStationSelected.value = false
        AppSingelton.exoPlayer = null
    }

    override fun onStationSearchListener(data: com.coderoids.radio.ui.search.searchedstationresponce.Data) {
        val playingChannelData = PlayingChannelData(data.url,data.favicon,data.name,data.id,"",data.country,"RADIO")
        AppSingelton._radioSelectedChannel.value = playingChannelData
        AppSingelton._isNewStationSelected.value = false
        AppSingelton.exoPlayer = null
    }

}