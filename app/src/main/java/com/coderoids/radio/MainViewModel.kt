package com.coderoids.radio

import android.util.Log
import android.util.Log.INFO
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val _isPlayerVisible = MutableLiveData<Boolean>()
    val isPlayerVisible : LiveData<Boolean> = _isPlayerVisible

    val _isPlayerFragVisible = MutableLiveData<Boolean>()
    val isPlayerFragVisible : LiveData<Boolean> = _isPlayerFragVisible

    fun getRadioListing(radioViewModel: RadioViewModel) {
        viewModelScope.launch {
            radioViewModel._radioListing.value = appRepository.getRadioListing()
        }
    }

    fun getPodCastListing(podcastViewModel: PodcastViewModel) {
        viewModelScope.launch {
            podcastViewModel._podcastListingMutable.value = appRepository.getPodCastListing()
        }
    }

    fun getLanguages(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._languageListingMutable.value  = appRepository.getLanguages()
        }
    }

    fun getCountires(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._countriesListingMutable.value  = appRepository.getCountries()
        }
    }

    fun getAllGenres(radioViewModel: RadioViewModel){
        viewModelScope.launch {
            radioViewModel._genresListinMutable.value  = appRepository.getAllGenres()
        }
    }
}