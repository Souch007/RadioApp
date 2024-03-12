package com.netcast.radio.request.repository

import com.netcast.radio.request.AppApis

class AppRepository (private val appApis: AppApis) : BaseRepository() {
    suspend fun getRadioListing(country:String) = safeApiCall {
        appApis.getRadioStations(country)
    }

    suspend fun getalternateChannels() = safeApiCall {
        appApis.alternateChannels()
    }

    suspend fun blockStation(id:String) = safeApiCall {
        appApis.blockStation(id)
    }
    suspend fun unblockStation(id:String) = safeApiCall {
        appApis.unblockStation(id)
    }

    suspend fun getPodCastListing(country:String) = safeApiCall {
        appApis.getPodCastStations(country)
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

    suspend fun getFrequentSearchTags(device_id:String) = safeApiCall{
        appApis.getFrequentSearches(device_id)
    }

    suspend fun searchPodcasts(searchedQuery : String,device_id:String) = safeApiCall{
        appApis.searchPodcast(searchedQuery,device_id)
    }

    suspend fun searchedStation(searchedQuery: String,device_id:String) = safeApiCall{
        appApis.searchStations(searchedQuery,device_id)
    }

    suspend fun getPodcastEpisodes(idPodcast: String) = safeApiCall {
        appApis.getPodcastEpisodes(idPodcast,)
    }
}