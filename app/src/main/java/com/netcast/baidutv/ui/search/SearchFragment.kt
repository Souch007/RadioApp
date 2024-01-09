package com.netcast.baidutv.ui.search

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.baidutv.MainViewModel
import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseFragment
import com.netcast.baidutv.databinding.FragmentSearchBinding
import com.netcast.baidutv.request.Resource
import com.netcast.baidutv.ui.search.adapters.PodcastSearchedAdapter
import com.netcast.baidutv.ui.search.adapters.StationSearchedAdapter

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search){
    private val _fragmentSearchViewModel : SearchViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentSearchBinding.initialize() {
        binding.fragmentSearchViewModel = _fragmentSearchViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        _fragmentSearchViewModel.frequentSearchResponce.observe(this@SearchFragment){
            try {
                val data = (it as Resource.Success).value.data
               val searchTags = data.filter {data->
                   data.q.isNotEmpty()
                }.distinctBy {distinctby->
                   distinctby.q
               }
                _fragmentSearchViewModel._frequestSearchList.value=searchTags
                binding.tagsadapter = com.netcast.baidutv.ui.search.adapters.SearchTagsAdapter(
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

    override fun onStart() {
        super.onStart()
     /*  val device_id = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        mainActivityViewModel.getFrequentSearchesTags(device_id, _fragmentSearchViewModel)*/
    }
}