package com.coderoids.radio.ui.radio

import androidx.fragment.app.activityViewModels
import androidx.navigation.navOptions
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioBinding
import com.coderoids.radio.request.Resource


class RadioFragment : BaseFragment<FragmentRadioBinding>(R.layout.fragment_radio) {

    val radioViewModel :RadioViewModel by activityViewModels()
    override fun FragmentRadioBinding.initialize() {
        binding.lifecycleOwner =this@RadioFragment
        binding.radioDataBinding =radioViewModel

        radioViewModel.radioListing.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel.radioListArray.value = data.publicRadio
                binding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)

                radioViewModel._radioPopListArray.value = data.pop

                binding.popadapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)

                radioViewModel._radioNewsListArray.value = data.news
                binding.newsadapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)
            } catch (ex : java.lang.Exception){
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if(failure == 400 && responseBody == null){

                }
            }
        }
    }

}