package com.coderoids.radio.request

import com.coderoids.radio.ui.radio.data.RadioData
import retrofit2.http.GET

interface AppApis {
@GET(AppConstants.FETCH_RADIO)
suspend fun getRadioStations() : RadioData

@GET(AppConstants.FETCH_RADIO_POP_ROCK)
suspend  fun getPopRockRadioStations() : RadioData

@GET(AppConstants.FETCH_NEWS_CULTURE)
suspend  fun getRadioNewsStations() : RadioData
}