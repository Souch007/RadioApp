package com.netcast.radio.ui.radio

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.ActivityFilterRadioBinding
import com.netcast.radio.databinding.FragmentAllgenreBinding
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.search.SearchViewModel
import com.netcast.radio.ui.search.adapters.StationSearchedAdapter

class AllGenreFragment() :
    BaseFragment<FragmentAllgenreBinding>(R.layout.fragment_allgenre) {

    private lateinit var mainActivityViewModel: MainViewModel
    private val TAG = "FilterRadioFragment"
    val radioViewModel: RadioViewModel by activityViewModels()

    override fun FragmentAllgenreBinding.initialize() {
        binding.viewmodel=radioViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }


        radioViewModel._genresListinLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                val sortedList = data.sortedBy { it.name }
                binding.genresAdapter = com.netcast.radio.ui.radio.adapter.GenresAdapter(
                    listOf(),
                    mainActivityViewModel,
                   "all"
                )
                radioViewModel._genresListArray.value = sortedList
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
 /*       mainActivityViewModel._queriedSearched.observe(viewLifecycleOwner) {
            it?.let {
                val args = Bundle()
                args.putString("filter_tag", it)
                findNavController().navigate(R.id.navigation_filterstaions,args)

            }
        }*/

    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getAllGenres(radioViewModel)
        binding.tvFilter.text = "All Genre"
    }

}