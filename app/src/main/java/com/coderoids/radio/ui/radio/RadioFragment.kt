package com.coderoids.radio.ui.radio

import androidx.fragment.app.activityViewModels
import androidx.navigation.navOptions
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioBinding
import com.coderoids.radio.request.Resource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.request.repository.BaseRepository

class RadioFragment : BaseFragment<FragmentRadioBinding>(R.layout.fragment_radio) {

    val radioViewModel :RadioViewModel by activityViewModels()
    override fun FragmentRadioBinding.initialize() {
        binding.lifecycleOwner =this@RadioFragment
        binding.radioDataBinding =radioViewModel

        radioViewModel.radioListing.observe(this@RadioFragment) {
            val data = (it as Resource.Success).value.data
            radioViewModel.radioListArray.value = data
            binding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)
        }

        radioViewModel.radioPopListing.observe(this@RadioFragment){
            val data = (it as Resource.Success).value.data
            radioViewModel._radioPopListArray.value = data
            binding.popadapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)

        }

        radioViewModel.radioNewsListing.observe(this@RadioFragment){
            val data = (it as Resource.Success).value.data
            radioViewModel._radioNewsListArray.value = data
            binding.newsadapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)

        }

    }

}