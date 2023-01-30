package com.netcast.radio.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netcast.radio.request.Resource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.search.adapters.OnSearchTagListener
import com.netcast.radio.ui.search.adapters.PodSearchOnClickListener
import com.netcast.radio.ui.search.adapters.StationSearchListener
import com.netcast.radio.ui.search.frequentsearch.Data
import com.netcast.radio.ui.search.frequentsearch.FrequentSearchResponce
import com.netcast.radio.ui.search.searchedpodresponce.SearchedReponcePod
import com.netcast.radio.ui.search.searchedstationresponce.SearchedResponceStation

class SearchViewModel(appRepository: AppRepository) : ViewModel() , OnSearchTagListener ,
    PodSearchOnClickListener, StationSearchListener {
    var _frequentSearchesTags = MutableLiveData<Resource<FrequentSearchResponce>>()
    val frequentSearchResponce : LiveData<Resource<FrequentSearchResponce>> = _frequentSearchesTags

    val _frequestSearchList = MutableLiveData<List<Data>>()
    val frequentSearchList :LiveData<List<Data>> = _frequestSearchList

    var _searchResultsPodcast = MutableLiveData<Resource<SearchedReponcePod>>()
    val searchResultsPodcast : LiveData<Resource<SearchedReponcePod>> = _searchResultsPodcast

    val _searchListPodcast = MutableLiveData<List<com.netcast.radio.ui.search.searchedpodresponce.Data>>()
    val searchListPodcast :LiveData<List<com.netcast.radio.ui.search.searchedpodresponce.Data>> = _searchListPodcast

    var _searchResultsStations = MutableLiveData<Resource<SearchedResponceStation>>()
    val searchResultsStations : LiveData<Resource<SearchedResponceStation>> = _searchResultsStations

    val _searchListStations = MutableLiveData<List<com.netcast.radio.ui.search.searchedstationresponce.Data>>()
    val searchListStations :LiveData<List<com.netcast.radio.ui.search.searchedstationresponce.Data>> = _searchListStations
    override fun onSearchTagClicked(data: Data) {

    }

    override fun onPodCastSearchedListener(data: com.netcast.radio.ui.search.searchedpodresponce.Data) {
        Log.d("TAG", "onPodCastSearchedListener: ")
    }

    override fun onStationSearchListener(data: com.netcast.radio.ui.search.searchedstationresponce.Data) {
        Log.d("TAG", "onPodCastSearchedListener: ")

    }
}