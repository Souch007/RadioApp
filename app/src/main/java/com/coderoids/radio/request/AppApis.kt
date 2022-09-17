package com.coderoids.radio.request

import com.coderoids.radio.ui.radio.data.RadioData
import retrofit2.http.GET

interface AppApis {
@GET(AppConstants.FETCH_RADIO)
suspend fun getRadioStations() : RadioData
}