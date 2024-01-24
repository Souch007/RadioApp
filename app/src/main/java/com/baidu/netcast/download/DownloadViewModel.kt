package com.baidu.netcast.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.baidu.netcast.base.BaseViewModel
import com.baidu.netcast.download.adapter.OnClickEpisodeDownload
import com.baidu.netcast.request.repository.AppRepository
import com.baidu.netcast.ui.radioplayermanager.episodedata.Data

class DownloadViewModel(var appRepository: AppRepository) : BaseViewModel() ,
    OnClickEpisodeDownload {
    var _listDownloadedEpisodes = MutableLiveData<List<Data>>()
    var listDownloadedEpisodes : LiveData<List<Data>> = _listDownloadedEpisodes

    var onDownloadPlayListner = MutableLiveData<Data>()

    override fun onDownloadedEpisodeClicked(data: Data) {
        onDownloadPlayListner.value = data
    }

}