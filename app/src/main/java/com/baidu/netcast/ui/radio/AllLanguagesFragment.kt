package com.baidu.netcast.ui.radio

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseFragment
import com.baidu.netcast.databinding.FragmentAlllanguagesBinding
import com.baidu.netcast.request.Resource

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
                radioViewModel._langListArray.value = data
                binding.languagesAdapter =
                   com.baidu.netcast.ui.radio.adapter.LanguagesAdapter(
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