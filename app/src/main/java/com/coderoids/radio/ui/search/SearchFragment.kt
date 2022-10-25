package com.coderoids.radio.ui.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentSearchBinding
import com.coderoids.radio.request.Resource
import com.coderoids.radio.ui.search.adapters.PodSearchOnClickListener
import com.coderoids.radio.ui.search.adapters.PodcastSearchedAdapter
import com.coderoids.radio.ui.search.adapters.StationSearchListener
import com.coderoids.radio.ui.search.adapters.StationSearchedAdapter
import com.coderoids.radio.ui.search.searchedpodresponce.Data

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search){
    val _fragmentSearchViewModel : SearchViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentSearchBinding.initialize() {
        binding.fragmentSearchViewModel = _fragmentSearchViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        _fragmentSearchViewModel.frequentSearchResponce.observe(this@SearchFragment){
            val data = (it as Resource.Success).value.data
            _fragmentSearchViewModel._frequestSearchList.value = data
            binding.tagsadapter = com.coderoids.radio.ui.search.adapters.SearchTagsAdapter(
                listOf(),
                mainActivityViewModel
            )
        }

        _fragmentSearchViewModel.searchResultsPodcast.observe(this@SearchFragment){
            val data = (it as Resource.Success).value.data
            _fragmentSearchViewModel._searchListPodcast.value = data
            // binding.countriesAdapter =
            //                    com.coderoids.radio.ui.radio.adapter.CountriesAdapter(listOf(), radioViewModel)
            binding.podsearchadapter = PodcastSearchedAdapter(listOf(),mainActivityViewModel)
        }

        _fragmentSearchViewModel.searchResultsStations.observe(this@SearchFragment){
            val data = (it as Resource.Success).value.data
            _fragmentSearchViewModel._searchListStations.value = data
            binding.stationsearchadapter = StationSearchedAdapter(listOf(),mainActivityViewModel)
        }
    }
}