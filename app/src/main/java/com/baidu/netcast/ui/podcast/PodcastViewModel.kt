package com.baidu.netcast.ui.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baidu.netcast.request.Resource
import com.baidu.netcast.request.repository.AppRepository
import com.baidu.netcast.ui.podcast.poddata.PodListData
import com.baidu.netcast.ui.podcast.poddata.PodResponce


class PodcastViewModel(private val appRepository: AppRepository) : ViewModel() {

    val _podcastListingMutable = MutableLiveData<Resource<PodResponce>>()
    val podcastListingLive: LiveData<Resource<PodResponce>> = _podcastListingMutable
    val _podcastListArray = MutableLiveData<List<PodListData>>()
    val podcastListArray: LiveData<List<PodListData>> = _podcastListArray


    //____________Pod cast
    //_________________________________News Adapter______________________//
    val _newsArrayM = MutableLiveData<List<PodListData>>()
    val newsArrayLM: LiveData<List<PodListData>> = _newsArrayM
    //_________________________________Society Adapter______________________//
    val _fitnessM = MutableLiveData<List<PodListData>>()
    val fitnessLM: LiveData<List<PodListData>> = _fitnessM

    //_________________________________Business Adapter______________________//
    val _businessM = MutableLiveData<List<PodListData>>()
    val businessLM: LiveData<List<PodListData>> = _businessM

    //_________________________________Culture Adapter______________________//
    val _cultureM = MutableLiveData<List<PodListData>>()
    val cultureLM: LiveData<List<PodListData>> = _cultureM

    //_________________________________Education Adapter______________________//
    val _educationM = MutableLiveData<List<PodListData>>()
    val educationM: LiveData<List<PodListData>> = _educationM


}