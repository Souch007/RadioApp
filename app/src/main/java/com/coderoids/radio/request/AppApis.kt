package com.coderoids.radio.request

import com.coderoids.radio.ui.podcast.poddata.PodResponce
import com.coderoids.radio.ui.radio.genres.Genres
import com.coderoids.radio.ui.radio.countries.Countries
import com.coderoids.radio.ui.radio.data.temp.RadioResponse
import com.coderoids.radio.ui.radio.lanuages.Lanuages
import com.coderoids.radio.ui.search.frequentsearch.FrequentSearchResponce
import com.coderoids.radio.ui.search.searchedpodresponce.SearchedReponcePod
import com.coderoids.radio.ui.search.searchedstationresponce.SearchedResponceStation
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AppApis {
@GET(AppConstants.FETCH_RADIO)
suspend fun getRadioStations() : RadioResponse

@GET(AppConstants.PODCAST_LISTING)
suspend  fun getPodCastStations() : PodResponce

@GET(AppConstants.GET_LANGUAGES)
suspend  fun getLanguages() : Lanuages

@GET(AppConstants.GET_ALL_COUNTRIES)
suspend  fun getCountries() : Countries

@GET(AppConstants.GET_ALL_GENRES)
suspend  fun getAllGenres() : Genres

@GET(AppConstants.GET_FREQUENT_SEARCH)
suspend  fun getFrequentSearches() : FrequentSearchResponce

@GET(AppConstants.SEARCH+"?type=podcasts&")
suspend  fun searchPodcast(@Query("q") productId: String) : SearchedReponcePod

@GET(AppConstants.SEARCH+"?type=channels&")
suspend  fun searchStations(@Query("q") productId: String) : SearchedResponceStation
}