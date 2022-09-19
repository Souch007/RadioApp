package com.coderoids.radio

import android.util.Log
import android.util.Log.INFO
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.podcast.PodcastViewModel
import com.coderoids.radio.ui.radio.RadioViewModel
import kotlinx.coroutines.launch
import okhttp3.internal.platform.Platform.Companion.INFO
import java.util.logging.Level.INFO

class MainViewModel : ViewModel(){

    val remoteDataSource = RemoteDataSource()
    val appRepository = AppRepository(remoteDataSource.buildApi(AppApis::class.java))


    fun getRadioListing(radioViewModel: RadioViewModel) {
        viewModelScope.launch {
            radioViewModel._radioListing.value = appRepository.getRadioListing()
            getRadioPopListing(radioViewModel)
        }
    }

    fun getRadioPopListing(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._radioPopListing.value = appRepository.getPopRockRadioListing()
            getRadioNewsListing(radioViewModel)

        }
    }

    fun getRadioNewsListing(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._radioNewsListing.value = appRepository.getRadioNewsListing()
        }
    }

    fun getPodCastListing(podcastViewModel: PodcastViewModel) {
        viewModelScope.launch {
            podcastViewModel._podcastListingMutable.value = appRepository.getPodCastListing()
        }
    }
}