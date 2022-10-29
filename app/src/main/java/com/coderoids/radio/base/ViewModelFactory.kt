package com.coderoids.radio.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.request.repository.BaseRepository
import com.coderoids.radio.ui.favourites.FavouritesViewModel
import com.coderoids.radio.ui.podcast.PodcastViewModel
import com.coderoids.radio.ui.radio.RadioViewModel
import com.coderoids.radio.ui.radioplayermanager.RadioPlayerAVM
import com.coderoids.radio.ui.search.SearchViewModel
import com.coderoids.radio.ui.seeall.SeeAllViewModel

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
            return RadioPlayerAVM() as T
        else throw IllegalArgumentException("ViewModel Class Not Found")
    }
}