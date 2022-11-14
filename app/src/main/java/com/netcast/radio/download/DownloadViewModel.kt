package com.netcast.radio.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcast.radio.base.BaseViewModel
import com.netcast.radio.download.adapter.OnClickEpisodeDownload
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

class DownloadViewModel(var appRepository: AppRepository) : BaseViewModel() ,
    OnClickEpisodeDownload {
    var _listDownloadedEpisodes = MutableLiveData<List<Data>>()
    var listDownloadedEpisodes : LiveData<List<Data>> = _listDownloadedEpisodes

    var onDownloadPlayListner = MutableLiveData<Data>()

    override fun onDownloadedEpisodeClicked(data: Data) {
        onDownloadPlayListner.value = data
    }

}