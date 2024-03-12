package com.netcast.radio.ui.search

import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentSearchBinding
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.search.adapters.PodcastSearchedAdapter
import com.netcast.radio.ui.search.adapters.SearchTagsAdapter
import com.netcast.radio.ui.search.adapters.StationSearchedAdapter

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val _fragmentSearchViewModel: SearchViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    var appDatabase: AppDatabase? = null
    override fun FragmentSearchBinding.initialize() {
        appDatabase = initializeDB(requireContext())
        binding.fragmentSearchViewModel = _fragmentSearchViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }

        _fragmentSearchViewModel.frequentSearchResponce.observe(this@SearchFragment) {
            try {

                val data = (it as Resource.Success).value.data
                val searchTags = data.filter { data ->
                    data.q.isNotEmpty()
                }.distinctBy { distinctby ->
                    distinctby.q
                }
                _fragmentSearchViewModel._frequestSearchList.value = searchTags
                binding.tagsadapter = SearchTagsAdapter(
                    listOf(),
                    mainActivityViewModel
                )

            } catch (ex: Exception) {
                ex.printStackTrace()
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {

                }
            }
        }

        _fragmentSearchViewModel.searchResultsPodcast.observe(this@SearchFragment) {
            try {

                val data = (it as Resource.Success).value.data
                _fragmentSearchViewModel._searchListPodcast.value = data
                binding.podsearchadapter = PodcastSearchedAdapter(listOf(), mainActivityViewModel)
            } catch (ex: Exception) {
                ex.printStackTrace()
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {
                }
            }
        }

        _fragmentSearchViewModel.searchResultsStations.observe(this@SearchFragment) {
            try {
                /*when(it)
                {
                    is Resource.Failure -> {

                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {}
                }*/
                binding.searchResultsStations.visibility = View.VISIBLE
                val data = (it as Resource.Success).value.data
                _fragmentSearchViewModel._searchListStations.value = data
                binding.stationsearchadapter =
                    StationSearchedAdapter(listOf(), mainActivityViewModel)
            } catch (ex: Exception) {
                ex.printStackTrace()
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {

                }
            }

        }
    }

}