package com.netcast.baidutv.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcast.baidutv.base.BaseViewModel
import com.netcast.baidutv.request.Resource
import com.netcast.baidutv.request.repository.AppRepository
import com.netcast.baidutv.ui.search.adapters.OnSearchTagListener
import com.netcast.baidutv.ui.search.adapters.PodSearchOnClickListener
import com.netcast.baidutv.ui.search.adapters.StationSearchListener
import com.netcast.baidutv.ui.search.frequentsearch.Data
import com.netcast.baidutv.ui.search.frequentsearch.FrequentSearchResponce
import com.netcast.baidutv.ui.search.searchedpodresponce.SearchedReponcePod
import com.netcast.baidutv.ui.search.searchedstationresponce.SearchedResponceStation

class SearchViewModel(appRepository: AppRepository) : BaseViewModel() , OnSearchTagListener ,
    PodSearchOnClickListener, StationSearchListener {
    var _frequentSearchesTags = MutableLiveData<Resource<FrequentSearchResponce>>()
    val frequentSearchResponce : LiveData<Resource<FrequentSearchResponce>> = _frequentSearchesTags

    val _frequestSearchList = MutableLiveData<List<Data>>()
    val frequentSearchList :LiveData<List<Data>> = _frequestSearchList

    var _searchResultsPodcast = MutableLiveData<Resource<SearchedReponcePod>>()
    val searchResultsPodcast : LiveData<Resource<SearchedReponcePod>> = _searchResultsPodcast

    val _searchListPodcast = MutableLiveData<List<com.netcast.baidutv.ui.search.searchedpodresponce.Data>>()
    val searchListPodcast :LiveData<List<com.netcast.baidutv.ui.search.searchedpodresponce.Data>> = _searchListPodcast

    var _searchResultsStations = MutableLiveData<Resource<SearchedResponceStation>>()
    val searchResultsStations : LiveData<Resource<SearchedResponceStation>> = _searchResultsStations

    val _searchListStations = MutableLiveData<List<com.netcast.baidutv.ui.search.searchedstationresponce.Data>>()
    val searchListStations :LiveData<List<com.netcast.baidutv.ui.search.searchedstationresponce.Data>> = _searchListStations
    override fun onSearchTagClicked(data: Data) {

    }

    override fun onPodCastSearchedListener(data: com.netcast.baidutv.ui.search.searchedpodresponce.Data) {
        //Log("TAG", "onPodCastSearchedListener: ")
    }

    override fun onStationSearchListener(data: com.netcast.baidutv.ui.search.searchedstationresponce.Data) {
        //Log("TAG", "onPodCastSearchedListener: ")

    }
}