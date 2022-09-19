package com.coderoids.radio.request.repository

import com.coderoids.radio.request.AppApis

class AppRepository (private val appApis: AppApis) : BaseRepository() {
    suspend fun getRadioListing() = safeApiCall {
        appApis.getRadioStations()
    }

    suspend fun getPopRockRadioListing() = safeApiCall {
        appApis.getPopRockRadioStations()
    }

    suspend fun getRadioNewsListing() = safeApiCall {
        appApis.getRadioNewsStations()
    }

    suspend fun getPodCastListing() = safeApiCall {
        appApis.getPodCastStations()
    }
}