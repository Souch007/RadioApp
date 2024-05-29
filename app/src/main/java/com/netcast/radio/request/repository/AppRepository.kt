package com.netcast.radio.request.repository

import com.netcast.radio.request.AppApis

class AppRepository (private val appApis: AppApis) : BaseRepository() {
    suspend fun getRadioListing(country:String) = safeApiCall {
        appApis.getRadioStations(country)
    }

    suspend fun getMoreRadioListing(name:String,limit:Int,skip:Int) = safeApiCall {
        appApis.getRadioMoreStations(name,limit,skip)
    }

    suspend fun getalternateChannels(name: String) = safeApiCall {
        appApis.alternateChannels(name)
    }
    suspend fun setstatics(name:String,id :String, type :String,country:String,deviceid:String) = safeApiCall {
        appApis.setstatics(name,id,type,country,deviceid)
    }

    suspend fun blockStation(id:String) = safeApiCall {
        appApis.blockStation(id)
    }
    suspend fun unblockStation(id:String) = safeApiCall {
        appApis.unblockStation(id)
    }
    suspend fun notifyAppKilled(id:String,country: String,appIntime:String,appouttime: String) = safeApiCall {
        appApis.notifyAppKilled(id,country,appIntime,appouttime)
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

    suspend fun searchedStation(limit : Int ,searchedQuery: String,device_id:String) = safeApiCall{
        appApis.searchStations(limit,searchedQuery,device_id)
    }

    suspend fun getPodcastEpisodes(idPodcast: String) = safeApiCall {
        appApis.getPodcastEpisodes(idPodcast,)
    }
}