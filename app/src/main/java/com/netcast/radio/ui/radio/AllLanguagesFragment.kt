package com.netcast.radio.ui.radio

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentAllcountriesBinding
import com.netcast.radio.databinding.FragmentAlllanguagesBinding
import com.netcast.radio.request.Resource

class AllLanguagesFragment() :
    BaseFragment<FragmentAlllanguagesBinding>(R.layout.fragment_alllanguages) {

    private lateinit var mainActivityViewModel: MainViewModel
    private val TAG = "FilterRadioFragment"
    val radioViewModel: RadioViewModel by activityViewModels()

    override fun FragmentAlllanguagesBinding.initialize() {
        binding.viewmodel=radioViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }


        radioViewModel.languagesListingLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                val sortedList = data.sortedBy { it.name }
                radioViewModel._langListArray.value = sortedList
                binding.languagesAdapter =
                    com.netcast.radio.ui.radio.adapter.LanguagesAdapter(
                        listOf(),
                        mainActivityViewModel,
                        "all"
                    )
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getLanguages(radioViewModel)
        binding.tvFilter.text = "All Languages"
    }

}