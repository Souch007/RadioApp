package com.coderoids.radio.ui.radioplayermanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseViewModel
import com.coderoids.radio.request.Resource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.radioplayermanager.adapter.OnEpisodeClickListener
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data
import com.coderoids.radio.ui.radioplayermanager.episodedata.PodEpisodesData
import kotlinx.coroutines.launch

class RadioPlayerAVM(var appRepository: AppRepository) : BaseViewModel() , OnClickListnerRadio,OnEpisodeClickListener {
     var suggestedRadioList: List<RadioLists>? = null

    val _podEpisodesList = MutableLiveData<Resource<PodEpisodesData>>()
    val podEpisodesList: LiveData<Resource<PodEpisodesData>> = _podEpisodesList

    val _podEpisodeArray = MutableLiveData<List<Data>>()
    val podEpisodeArray: LiveData<List<Data>> = _podEpisodeArray

    val _episodeSelected = MutableLiveData<Data>()
    val _episodeDownloadSelected = MutableLiveData<Data>()

    override fun onRadioClicked(data: RadioLists) {
        TODO("Not yet implemented")
    }

    fun getPodcastEpisodes(idPodcast : String){
        viewModelScope.launch {
            _podEpisodesList.value = appRepository.getPodcastEpisodes(idPodcast)
        }
    }

    override fun onEpisodeClicked(data: Data) {
        _episodeSelected.value = data;
    }

    override fun onEpisodeDownloadClicked(data: Data) {
        if(!AppSingelton.downloadedIds.contains(data._id.toString().toRegex()) &&
            !AppSingelton.currentDownloading.matches(data._id.toString().toRegex())){
            _episodeDownloadSelected.value = data
        } else
            _episodeSelected.value = data;
    }
}