package com.baidu.netcast.ui.search

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseFragment
import com.baidu.netcast.databinding.FragmentSearchBinding
import com.baidu.netcast.db.AppDatabase
import com.baidu.netcast.request.Resource
import com.baidu.netcast.ui.search.adapters.PodcastSearchedAdapter
import com.baidu.netcast.ui.search.adapters.SearchTagsAdapter
import com.baidu.netcast.ui.search.adapters.StationSearchedAdapter

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search){
    private val _fragmentSearchViewModel : SearchViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel
    var appDatabase: AppDatabase? = null
    override fun FragmentSearchBinding.initialize() {
        appDatabase = initializeDB(requireContext())
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
                binding.tagsadapter = SearchTagsAdapter(
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
                binding.searchResultsPodcasts.visibility=View.VISIBLE
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

                binding.searchResultsStations.visibility=View.VISIBLE
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