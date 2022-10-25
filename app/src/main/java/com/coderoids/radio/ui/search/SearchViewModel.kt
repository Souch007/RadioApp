package com.coderoids.radio.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coderoids.radio.request.Resource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.podcast.poddata.PodResponce
import com.coderoids.radio.ui.search.adapters.OnSearchTagListener
import com.coderoids.radio.ui.search.adapters.PodSearchOnClickListener
import com.coderoids.radio.ui.search.adapters.StationSearchListener
import com.coderoids.radio.ui.search.frequentsearch.Data
import com.coderoids.radio.ui.search.frequentsearch.FrequentSearchResponce
import com.coderoids.radio.ui.search.searchedpodresponce.SearchedReponcePod
import com.coderoids.radio.ui.search.searchedstationresponce.SearchedResponceStation

class SearchViewModel(appRepository: AppRepository) : ViewModel() , OnSearchTagListener ,
    PodSearchOnClickListener, StationSearchListener {
    var _frequentSearchesTags = MutableLiveData<Resource<FrequentSearchResponce>>()
    val frequentSearchResponce : LiveData<Resource<FrequentSearchResponce>> = _frequentSearchesTags

    val _frequestSearchList = MutableLiveData<List<Data>>()
    val frequentSearchList :LiveData<List<Data>> = _frequestSearchList

    var _searchResultsPodcast = MutableLiveData<Resource<SearchedReponcePod>>()
    val searchResultsPodcast : LiveData<Resource<SearchedReponcePod>> = _searchResultsPodcast

    val _searchListPodcast = MutableLiveData<List<com.coderoids.radio.ui.search.searchedpodresponce.Data>>()
    val searchListPodcast :LiveData<List<com.coderoids.radio.ui.search.searchedpodresponce.Data>> = _searchListPodcast

    var _searchResultsStations = MutableLiveData<Resource<SearchedResponceStation>>()
    val searchResultsStations : LiveData<Resource<SearchedResponceStation>> = _searchResultsStations

    val _searchListStations = MutableLiveData<List<com.coderoids.radio.ui.search.searchedstationresponce.Data>>()
    val searchListStations :LiveData<List<com.coderoids.radio.ui.search.searchedstationresponce.Data>> = _searchListStations
    override fun onSearchTagClicked(data: Data) {
    }

    override fun onPodCastSearchedListener(data: com.coderoids.radio.ui.search.searchedpodresponce.Data) {
    }

    override fun onStationSearchListener(data: com.coderoids.radio.ui.search.searchedstationresponce.Data) {
    }
}