package com.coderoids.radio.request

import com.coderoids.radio.ui.podcast.data.PodcastData
import com.coderoids.radio.ui.radio.data.temp.RadioResponse
import retrofit2.http.GET

interface AppApis {
@GET(AppConstants.FETCH_RADIO)
suspend fun getRadioStations() : RadioResponse

@GET(AppConstants.PODCAST_LISTING)
suspend  fun getPodCastStations() : PodcastData
}