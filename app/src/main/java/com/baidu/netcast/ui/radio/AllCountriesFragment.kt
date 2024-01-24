package com.baidu.netcast.ui.radio

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseFragment
import com.baidu.netcast.databinding.FragmentAllcountriesBinding
import com.baidu.netcast.request.Resource

class AllCountriesFragment() :
    BaseFragment<FragmentAllcountriesBinding>(R.layout.fragment_allcountries) {

    private lateinit var mainActivityViewModel: MainViewModel
    private val TAG = "FilterRadioFragment"
    val radioViewModel: RadioViewModel by activityViewModels()

    override fun FragmentAllcountriesBinding.initialize() {
        binding.viewmodel = radioViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }


        radioViewModel.countriesListingLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._countriesListArray.value = data
                binding.countriesAdapter = com.baidu.netcast.ui.radio.adapter.CountriesAdapter(
                    listOf(), mainActivityViewModel, "all"
                )
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }/*       mainActivityViewModel._queriedSearched.observe(viewLifecycleOwner) {
                   it?.let {
                       val args = Bundle()
                       args.putString("filter_tag", it)
                       findNavController().navigate(R.id.navigation_filterstaions,args)

                   }
               }*/

    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getCountires(radioViewModel)
        binding.tvFilter.text = "All Countires"
    }

}