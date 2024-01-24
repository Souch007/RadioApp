package com.baidu.netcast.ui.radio

import android.provider.Settings
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseFragment
import com.baidu.netcast.databinding.ActivityFilterRadioBinding
import com.baidu.netcast.request.Resource
import com.baidu.netcast.ui.search.SearchViewModel
import com.baidu.netcast.ui.search.adapters.StationSearchedAdapter

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
                    //Log(TAG, "SearchData: $searchData")
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