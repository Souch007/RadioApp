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

    suspend fun getCountries() = safeApiCall {
        appApis.getCountries()
    }

    suspend fun getAllGenres() = safeApiCall {
        appApis.getAllGenres()
    }

    suspend fun getFrequentSearchTags() = safeApiCall{
        appApis.getFrequentSearches()
    }

    suspend fun searchPodcasts(searchedQuery : String) = safeApiCall{
        appApis.searchPodcast(searchedQuery)
    }

    suspend fun searchedStation(searchedQuery: String) = safeApiCall{
        appApis.searchStations(searchedQuery)
    }

    suspend fun getPodcastEpisodes(idPodcast: String) = safeApiCall {
        appApis.getPodcastEpisodes(idPodcast,)
    }
}