package com.coderoids.radio.ui.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderoids.radio.request.Resource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.podcast.adapter.OnClickListenerPodcast
import com.coderoids.radio.ui.podcast.data.Feed
import com.coderoids.radio.ui.podcast.data.PodcastData
import com.coderoids.radio.ui.radio.data.Data
import com.coderoids.radio.ui.radio.data.RadioData
import kotlinx.coroutines.launch

class PodcastViewModel(private val appRepository: AppRepository) : ViewModel() , OnClickListenerPodcast{

    val _podcastListingMutable = MutableLiveData<Resource<PodcastData>>()
    val podcastListingLive: LiveData<Resource<PodcastData>> = _podcastListingMutable
    val _podcastListArray = MutableLiveData<List<Feed>>()
    val podcastListArray: LiveData<List<Feed>> = _podcastListArray
    //____________Pod cast

    fun getPodcastListing(){
        viewModelScope.launch {
            _podcastListingMutable.value = appRepository.getPodCastListing()
        }
    }

    override fun onPodCastClicked(data: Feed) {
        TODO("Not yet implemented")
    }

}