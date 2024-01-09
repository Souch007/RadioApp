package com.netcast.baidutv.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcast.baidutv.base.BaseViewModel
import com.netcast.baidutv.download.adapter.OnClickEpisodeDownload
import com.netcast.baidutv.request.repository.AppRepository
import com.netcast.baidutv.ui.radioplayermanager.episodedata.Data

class DownloadViewModel(var appRepository: AppRepository) : BaseViewModel() ,
    OnClickEpisodeDownload {
    var _listDownloadedEpisodes = MutableLiveData<List<Data>>()
    var listDownloadedEpisodes : LiveData<List<Data>> = _listDownloadedEpisodes

    var onDownloadPlayListner = MutableLiveData<Data>()

    override fun onDownloadedEpisodeClicked(data: Data) {
        onDownloadPlayListner.value = data
    }

}