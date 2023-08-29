package com.netcast.radio.ui.radio

import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.ActivityFilterRadioBinding
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.search.SearchViewModel
import com.netcast.radio.ui.search.adapters.StationSearchedAdapter

class FilterRadioFragment() :
    BaseFragment<ActivityFilterRadioBinding>(R.layout.activity_filter_radio) {
    private val searchviewModel: SearchViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    private val TAG = "FilterRadioFragment"

    override fun ActivityFilterRadioBinding.initialize() {
        binding.searchViewModel = searchviewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }


        searchviewModel.searchResultsStations.observe(viewLifecycleOwner) {
            binding.pbFilterstations.visibility=View.GONE
            it?.let { searchData ->
                try {
                    binding.searchResultsStations.visibility=View.VISIBLE
                    Log.d(TAG, "SearchData: $searchData")
                    val data = (searchData as Resource.Success).value.data
                    searchViewModel!!._searchListStations.value = data
                    binding.stationsearchadapter =
                        StationSearchedAdapter(listOf(), mainActivityViewModel)
                } catch (ex: Exception) {
                    binding.pbFilterstations.visibility=View.GONE
                    ex.printStackTrace()
                    val failure = (searchData as Resource.Failure).errorCode
                    val responseBody = searchData.errorResponseBody
                    if (failure == 400 && responseBody == null) {

                    }
                }
            }


        }

    }

    override fun onResume() {
        super.onResume()
        binding.pbFilterstations.visibility=View.VISIBLE
        binding.searchResultsStations.visibility=View.GONE
        val deviceID =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        mainActivityViewModel.getSearchQueryResult(
            deviceID, arguments?.getString("filter_tag") ?: "", searchviewModel
        )
        binding.tvFilter.text = arguments?.getString("filter_tag")
    }

}