package com.netcast.radio.ui.radioplayermanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseViewModel
import com.netcast.radio.request.Resource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.radio.adapter.OnClickListnerRadio
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.netcast.radio.ui.radioplayermanager.adapter.OnEpisodeClickListener
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import com.netcast.radio.ui.radioplayermanager.episodedata.PodEpisodesData
import kotlinx.coroutines.launch

class RadioPlayerAVM(var appRepository: AppRepository) : BaseViewModel(), OnClickListnerRadio,
    OnEpisodeClickListener {
    var suggestedRadioList: List<RadioLists>? = null

    val _podEpisodesList = MutableLiveData<Resource<PodEpisodesData>>()
    val podEpisodesList: LiveData<Resource<PodEpisodesData>> = _podEpisodesList

    val _podEpisodeArray = MutableLiveData<List<Data>>()
    val podEpisodeArray: LiveData<List<Data>> = _podEpisodeArray

    val _episodeSelected = MutableLiveData<Data>()
    val _radioClicked = MutableLiveData<RadioLists>()
    val _episodeDownloadSelected = MutableLiveData<Data>()
    val _onepisodeDeleteSelected = MutableLiveData<Data>()

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
        _radioClicked.value = data
    }

    fun getPodcastEpisodes(idPodcast: String) {
        viewModelScope.launch {
            _podEpisodesList.value = appRepository.getPodcastEpisodes(idPodcast)
        }
    }

    override fun onEpisodeClicked(data: Data) {
        _episodeSelected.value = data
    }

    override fun onEpisodeDownloadClicked(data: Data) {
        if (!AppSingelton.downloadedIds.contains(data._podid.toString().toRegex()) &&
            !AppSingelton.currentDownloading.matches(data._podid.toString().toRegex())
        ) {
            _episodeDownloadSelected.value = data
        } else
            _episodeSelected.value = data
    }

    override fun onEpisodeDeleteClicked(data: Data) {
        _onepisodeDeleteSelected.value = data
    }
}