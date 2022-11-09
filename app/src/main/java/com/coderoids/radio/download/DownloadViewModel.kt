package com.coderoids.radio.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coderoids.radio.base.BaseViewModel
import com.coderoids.radio.download.adapter.OnClickEpisodeDownload
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.radioplayermanager.adapter.OnEpisodeClickListener
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data

class DownloadViewModel(var appRepository: AppRepository) : BaseViewModel() ,
    OnClickEpisodeDownload {
    var _listDownloadedEpisodes = MutableLiveData<List<Data>>()
    var listDownloadedEpisodes : LiveData<List<Data>> = _listDownloadedEpisodes
    override fun onDownloadedEpisodeClicked(data: Data) {
    }

}