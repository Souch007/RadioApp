package com.netcast.radio.ui.search

import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.activityViewModels
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentSearchBinding
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.search.adapters.PodcastSearchedAdapter
import com.netcast.radio.ui.search.adapters.StationSearchedAdapter

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search){
    val _fragmentSearchViewModel : SearchViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentSearchBinding.initialize() {
        binding.fragmentSearchViewModel = _fragmentSearchViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        _fragmentSearchViewModel.frequentSearchResponce.observe(this@SearchFragment){
            try {
                val data = (it as Resource.Success).value.data
               val searchTags = data.filter {
                    it.q.isNotEmpty()
                }.distinctBy {
                    it.q
               }
                _fragmentSearchViewModel._frequestSearchList.value=searchTags
                binding.tagsadapter = com.netcast.radio.ui.search.adapters.SearchTagsAdapter(
                    listOf(),
                    mainActivityViewModel
                )

            } catch (ex : Exception){
                ex.printStackTrace()
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {

                }
            }
        }

        _fragmentSearchViewModel.searchResultsPodcast.observe(this@SearchFragment){
            try {
                val data = (it as Resource.Success).value.data
                _fragmentSearchViewModel._searchListPodcast.value = data
                // binding.countriesAdapter =
                //                    com.coderoids.radio.ui.radio.adapter.CountriesAdapter(listOf(), radioViewModel)
                binding.podsearchadapter = PodcastSearchedAdapter(listOf(),mainActivityViewModel)
            } catch (ex : Exception){
                ex.printStackTrace()
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {

                }
            }

        }

        _fragmentSearchViewModel.searchResultsStations.observe(this@SearchFragment){
            try {
                val data = (it as Resource.Success).value.data
                _fragmentSearchViewModel._searchListStations.value = data
                binding.stationsearchadapter = StationSearchedAdapter(listOf(),mainActivityViewModel)
            } catch (ex : Exception){
                ex.printStackTrace()
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {

                }
            }

        }
    }
}