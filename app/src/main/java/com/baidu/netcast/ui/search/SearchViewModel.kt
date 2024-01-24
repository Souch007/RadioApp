package com.baidu.netcast.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.baidu.netcast.base.BaseViewModel
import com.baidu.netcast.request.Resource
import com.baidu.netcast.request.repository.AppRepository
import com.baidu.netcast.ui.search.adapters.OnSearchTagListener
import com.baidu.netcast.ui.search.adapters.PodSearchOnClickListener
import com.baidu.netcast.ui.search.adapters.StationSearchListener
import com.baidu.netcast.ui.search.frequentsearch.Data
import com.baidu.netcast.ui.search.frequentsearch.FrequentSearchResponce
import com.baidu.netcast.ui.search.searchedpodresponce.SearchedReponcePod
import com.baidu.netcast.ui.search.searchedstationresponce.SearchedResponceStation

class SearchViewModel(appRepository: AppRepository) : BaseViewModel() , OnSearchTagListener ,
    PodSearchOnClickListener, StationSearchListener {
    var _frequentSearchesTags = MutableLiveData<Resource<FrequentSearchResponce>>()
    val frequentSearchResponce : LiveData<Resource<FrequentSearchResponce>> = _frequentSearchesTags

    val _frequestSearchList = MutableLiveData<List<Data>>()
    val frequentSearchList :LiveData<List<Data>> = _frequestSearchList

    var _searchResultsPodcast = MutableLiveData<Resource<SearchedReponcePod>>()
    val searchResultsPodcast : LiveData<Resource<SearchedReponcePod>> = _searchResultsPodcast

    val _searchListPodcast = MutableLiveData<List<com.baidu.netcast.ui.search.searchedpodresponce.Data>>()
    val searchListPodcast :LiveData<List<com.baidu.netcast.ui.search.searchedpodresponce.Data>> = _searchListPodcast

    var _searchResultsStations = MutableLiveData<Resource<SearchedResponceStation>>()
    val searchResultsStations : LiveData<Resource<SearchedResponceStation>> = _searchResultsStations

    val _searchListStations = MutableLiveData<List<com.baidu.netcast.ui.search.searchedstationresponce.Data>>()
    val searchListStations :LiveData<List<com.baidu.netcast.ui.search.searchedstationresponce.Data>> = _searchListStations
    override fun onSearchTagClicked(data: Data) {

    }

    override fun onPodCastSearchedListener(data: com.baidu.netcast.ui.search.searchedpodresponce.Data) {
        //Log("TAG", "onPodCastSearchedListener: ")
    }

    override fun onStationSearchListener(data: com.baidu.netcast.ui.search.searchedstationresponce.Data) {
        //Log("TAG", "onPodCastSearchedListener: ")

    }
}