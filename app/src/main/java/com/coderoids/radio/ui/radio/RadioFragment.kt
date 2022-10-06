package com.coderoids.radio.ui.radio

import androidx.fragment.app.activityViewModels
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioBinding
import com.coderoids.radio.request.Resource
import com.coderoids.radio.ui.radio.adapter.DotIndicatorAdapter
import com.coderoids.radio.util.ZoomOutPageTransformer
import org.json.JSONException


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

                radioViewModel._radioClassicallistingArry.value = data.classical
                binding.classicalAdapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)

                with(viewPager){
                    adapter = DotIndicatorAdapter(data.podcasts)
                    setPageTransformer(true, ZoomOutPageTransformer())
                    dotsIndicator.attachTo(this)
                }
            } catch (ex : java.lang.Exception){
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if(failure == 400 && responseBody == null){

                }
            }
        }

        radioViewModel.languagesListingLive.observe(this@RadioFragment){
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._langListArray.value = data
                binding.languagesAdapter = com.coderoids.radio.ui.radio.adapter.LanguagesAdapter(listOf(),radioViewModel)
            } catch (exception : JSONException){
                exception.printStackTrace()
            }
        }

        radioViewModel.countriesListingLive.observe(this@RadioFragment){
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._countriesListArray.value = data
                binding.countriesAdapter = com.coderoids.radio.ui.radio.adapter.CountriesAdapter(listOf(),radioViewModel)
            } catch (exception : JSONException){
                exception.printStackTrace()
            }
        }

        radioViewModel._genresListinLive.observe(this@RadioFragment){
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._genresListArray.value = data
                binding.genresAdapter = com.coderoids.radio.ui.radio.adapter.GenresAdapter(listOf(),radioViewModel)
            } catch (exception : JSONException){
                exception.printStackTrace()
            }
        }

    }

}