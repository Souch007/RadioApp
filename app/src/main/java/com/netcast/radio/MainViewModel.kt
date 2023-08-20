package com.netcast.radio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseViewModel
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.RemoteDataSource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.favourites.adapters.OnFavouriteClickListener
import com.netcast.radio.ui.podcast.PodcastViewModel
import com.netcast.radio.ui.podcast.adapter.OnClickListenerPodcast
import com.netcast.radio.ui.podcast.poddata.PodListData
import com.netcast.radio.ui.radio.FilterSearchListener
import com.netcast.radio.ui.radio.RadioViewModel
import com.netcast.radio.ui.radio.adapter.OnClickGeneresListener
import com.netcast.radio.ui.radio.adapter.OnClickListenerCountires
import com.netcast.radio.ui.radio.adapter.OnClickListenerLanguages
import com.netcast.radio.ui.radio.adapter.OnClickListnerRadio
import com.netcast.radio.ui.radio.countries.Data
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.netcast.radio.ui.search.SearchViewModel

import com.netcast.radio.ui.search.adapters.OnSearchTagListener
import com.netcast.radio.ui.search.adapters.PodSearchOnClickListener
import com.netcast.radio.ui.search.adapters.StationSearchListener
import com.netcast.radio.ui.seeall.adapter.OnClickListenerSeeAll
import com.netcast.radio.ui.seeall.adapter.OnClickListerPODSeeAll
import kotlinx.coroutines.launch
import kotlin.math.log

class MainViewModel : BaseViewModel(), OnClickListnerRadio, OnClickListenerPodcast,
    OnFavouriteClickListener,
    OnClickListenerSeeAll, OnClickListerPODSeeAll, OnClickListenerLanguages,
    OnClickListenerCountires, OnClickGeneresListener, OnSearchTagListener,
    PodSearchOnClickListener, StationSearchListener, FilterSearchListener {
    val remoteDataSource = RemoteDataSource()
    val appRepository = AppRepository(remoteDataSource.buildApi(AppApis::class.java))

    val _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean> get() = _state

    val _timerradio = MutableLiveData<String>()
    val radiotimer: LiveData<String> get() = _timerradio

    val _isStationActive = MutableLiveData<Boolean>()
    val isStationActive: LiveData<Boolean> = _isStationActive

    var _currentPlayingChannel = MutableLiveData<PlayingChannelData>()
    val currentPlayingChannel: LiveData<PlayingChannelData> = _currentPlayingChannel

    val _radioSelectedChannel = MutableLiveData<PlayingChannelData>()
    val radioSelectedChannel: LiveData<PlayingChannelData> = _radioSelectedChannel

    val _suggesteStations = MutableLiveData<List<RadioLists>>()
    val suggestedStations: LiveData<List<RadioLists>> = _suggesteStations

    val _favouritesRadio = MutableLiveData<List<PlayingChannelData>>()
    var favouritesRadioArray = mutableListOf<PlayingChannelData>()
    val favouritesRadio: LiveData<List<PlayingChannelData>> = _favouritesRadio

    //---------------------------------------------------------------------//
    var _selectedSeeAllListRadio = MutableLiveData<List<RadioLists>>()
    val selectedSeeAllListRadio: LiveData<List<RadioLists>> = _selectedSeeAllListRadio
    val _selectedSeeAllPodcasts = MutableLiveData<List<PodListData>>()
    val selectedSeeAllPodcasts: LiveData<List<PodListData>> = _selectedSeeAllPodcasts
    val _radioSeeAllSelected = MutableLiveData<String>()
    val _radioSelectedTitle = MutableLiveData<String>()

    //------------------------------------------------------------------//
    val _queriedSearched = MutableLiveData<String>()
    var valueTypeFrag: String = ""
    var currentFragmentId: String = "Radio"
    val navigateToPodcast = MutableLiveData<Boolean>()

    //----------------------------------//

    fun getRadioListing(radioViewModel: RadioViewModel, country: String?) {
        viewModelScope.launch {
            radioViewModel._radioListing.value = appRepository.getRadioListing(country ?: "")
            Log.d("MainViewModel", "getRadioListing: $country")
//            radioViewModel._radioListing.value = appRepository.getRadioListing("")
        }
    }

    fun getPodCastListing(podcastViewModel: PodcastViewModel, country: String?) {
        viewModelScope.launch {
//            podcastViewModel._podcastListingMutable.value = appRepository.getPodCastListing("")
            podcastViewModel._podcastListingMutable.value =
                appRepository.getPodCastListing(country ?: "")

        }
    }

    fun getLanguages(radioViewModel: RadioViewModel) {
        viewModelScope.launch {
            radioViewModel._languageListingMutable.value = appRepository.getLanguages()
        }
    }

    fun getCountires(radioViewModel: RadioViewModel) {
        viewModelScope.launch {
            radioViewModel._countriesListingMutable.value = appRepository.getCountries()
        }
    }

    fun getAllGenres(radioViewModel: RadioViewModel) {
        viewModelScope.launch {
            radioViewModel._genresListinMutable.value = appRepository.getAllGenres()
        }
    }

    fun getFrequentSearchesTags(device_id: String, searchViewModel: SearchViewModel) {
        viewModelScope.launch {
            searchViewModel._frequentSearchesTags.value =
                appRepository.getFrequentSearchTags(device_id)
        }
    }

    fun getSearchQueryResult(
        device_id: String,
        searchQuery: String,
        searchViewModel: SearchViewModel
    ) {
        viewModelScope.launch {
            try {
                searchViewModel._searchResultsPodcast.value =
                    appRepository.searchPodcasts(searchQuery, device_id)
                searchViewModel._searchResultsStations.value =
                    appRepository.searchedStation(searchQuery, device_id)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }


    override fun onRadioClicked(data: RadioLists) {

        var playingChannelData = PlayingChannelData(
            data.url,
            data.favicon,
            data.name,
            data.id,
            "",
            data.country,
            "RADIO"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
        if (AppSingelton._currenPlayingChannelId.matches(data.id.toRegex()))
            AppSingelton._isNewStationSelected.value = false
        else {
            AppSingelton._isNewStationSelected.value = true
            if (AppSingelton.exoPlayer != null) {
                AppSingelton.exoPlayer!!.stop()
                AppSingelton.exoPlayer!!.release()
            }
            AppSingelton.exoPlayer = null
        }

    }

    override fun onPodCastClicked(data: PodListData) {
        var playingChannelData = PlayingChannelData(
            data.website ?: "",
            data.image,
            data.title,
            data._id,
            data._id,
            data.publisher,
            "PODCAST"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
        if (AppSingelton._currenPlayingChannelId.matches(data.id.toRegex()))
            AppSingelton._isNewStationSelected.value = false
        else {
            AppSingelton._isNewStationSelected.value = true
            if (AppSingelton.exoPlayer != null) {
                AppSingelton.exoPlayer!!.stop()
                AppSingelton.exoPlayer!!.release()
            }
            AppSingelton.exoPlayer = null
        }
    }


    override fun onFavChannelClicked(playingChannelData: PlayingChannelData, tabtype: String) {
        Log.d("onFavChannelClicked", "onFavChannelClicked: $tabtype")
        AppSingelton._radioSelectedChannel.value = playingChannelData
        AppSingelton._isNewStationSelected.value = false
        if (AppSingelton.exoPlayer != null) {
            AppSingelton.exoPlayer!!.stop()
            AppSingelton.exoPlayer!!.release()
        }
        AppSingelton.exoPlayer = null
        if (tabtype.equals("favourites", true)) {
            val updatedList = AppSingelton.favouritesRadioArray
            updatedList?.remove(playingChannelData)
            updatedList?.add(0, playingChannelData)
            AppSingelton.favouritesRadioArray = updatedList
            AppSingelton._isFavUpdated.value = true
        } else {
            val recentPlayedupdatedList = AppSingelton.recentlyPlayedArray
            recentPlayedupdatedList?.remove(playingChannelData)
        recentPlayedupdatedList?.add(AppSingelton.recentlyPlayedArray.size,playingChannelData)
//            recentPlayedupdatedList?.add(0, playingChannelData)
            AppSingelton.recentlyPlayedArray = recentPlayedupdatedList
//        updatedList?.remove(playingChannelData)
//        AppSingelton.favouritesRadioArray=updatedList
            AppSingelton.isNewItemAdded.value = true

        }
    }

    override fun onFavChannelDeleteClicked(playingChannelData: PlayingChannelData) {
        try {
            for (i in AppSingelton.favouritesRadioArray.indices) {
                var data = AppSingelton.favouritesRadioArray[i]
                if (data.id == playingChannelData.id) {
                    AppSingelton.favouritesRadioArray.removeAt(i)
                }
            }
            AppSingelton._isFavUpdated.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    override fun OnClickGenres(data: com.netcast.radio.ui.radio.genres.Data) {
        _queriedSearched.value = data.name

    }

    override fun onLanguageClicked(data: com.netcast.radio.ui.radio.lanuages.Data) {
        _queriedSearched.value = data.name
    }

    override fun onSearchTagClicked(data: com.netcast.radio.ui.search.frequentsearch.Data) {
        _queriedSearched.value = data.q
    }

    override fun onPodCastSearchedListener(data: com.netcast.radio.ui.search.searchedpodresponce.Data) {
        val playingChannelData = PlayingChannelData(
            data.url,
            data.image,
            data.title,
            data._id,
            data._id,
            data.author,
            "PODCAST"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
        AppSingelton._isNewStationSelected.value = false
        AppSingelton.exoPlayer = null
    }

    override fun onStationSearchListener(data: com.netcast.radio.ui.search.searchedstationresponce.Data) {
        val playingChannelData = PlayingChannelData(
            data.url,
            data.favicon,
            data.name,
            data.id,
            "",
            data.country,
            "RADIO"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
        AppSingelton._isNewStationSelected.value = false
        AppSingelton.exoPlayer = null
    }

    override fun onFilterSearchListenerr(data: RadioLists) {
        onRadioClicked(data)
    }


}