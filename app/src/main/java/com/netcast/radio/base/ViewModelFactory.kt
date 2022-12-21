package com.netcast.radio.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.download.DownloadViewModel
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.request.repository.BaseRepository
import com.netcast.radio.ui.favourites.FavouritesViewModel
import com.netcast.radio.ui.podcast.PodcastViewModel
import com.netcast.radio.ui.radio.RadioViewModel
import com.netcast.radio.ui.radioplayermanager.RadioPlayerAVM
import com.netcast.radio.ui.search.SearchViewModel
import com.netcast.radio.ui.seeall.SeeAllViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PodcastViewModel::class.java)) {
            return PodcastViewModel(
                repository as AppRepository
            ) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
            ) as T
        }
        else if (modelClass.isAssignableFrom(FavouritesViewModel::class.java))
            return FavouritesViewModel(repository as AppRepository ) as T
        else if (modelClass.isAssignableFrom(SearchViewModel::class.java))
            return SearchViewModel(repository as AppRepository ) as T
        else if (modelClass.isAssignableFrom(RadioViewModel::class.java))
            return RadioViewModel() as T
        else if (modelClass.isAssignableFrom(SeeAllViewModel::class.java))
            return SeeAllViewModel() as T
        else if (modelClass.isAssignableFrom(RadioPlayerAVM::class.java))
            return RadioPlayerAVM(repository as AppRepository) as T
        else if (modelClass.isAssignableFrom(DownloadViewModel::class.java))
            return DownloadViewModel(repository as AppRepository) as T
        else throw IllegalArgumentException("ViewModel Class Not Found")
    }
}