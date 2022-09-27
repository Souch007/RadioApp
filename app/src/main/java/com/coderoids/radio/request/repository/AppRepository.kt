package com.coderoids.radio.request.repository

import com.coderoids.radio.request.AppApis

class AppRepository (private val appApis: AppApis) : BaseRepository() {
    suspend fun getRadioListing() = safeApiCall {
        appApis.getRadioStations()
    }

    suspend fun getPodCastListing() = safeApiCall {
        appApis.getPodCastStations()
    }

   suspend fun getLanguages() = safeApiCall {
        appApis.getLanguages()
    }
}