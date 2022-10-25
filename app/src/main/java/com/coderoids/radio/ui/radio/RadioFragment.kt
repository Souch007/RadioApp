package com.coderoids.radio.ui.radio

import android.content.Intent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioBinding
import com.coderoids.radio.request.Resource
import com.coderoids.radio.ui.radio.adapter.DotIndicatorAdapter
import com.coderoids.radio.util.ZoomOutPageTransformer
import org.json.JSONException


class RadioFragment : BaseFragment<FragmentRadioBinding>(R.layout.fragment_radio) {

    val radioViewModel: RadioViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentRadioBinding.initialize() {
        binding.lifecycleOwner = this@RadioFragment
        binding.radioDataBinding = radioViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        binding.shimmerLayout.stopShimmer()
        mainActivityViewModel.currentFragmentId = "Radio"
        radioViewModel.radioListing.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel.radioListArray.value = data.publicRadio
                binding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                    listOf(),
                    mainActivityViewModel
                )

                radioViewModel._radioPopListArray.value = data.pop
                binding.popadapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                    listOf(),
                    mainActivityViewModel
                )

                radioViewModel._radioNewsListArray.value = data.news
                binding.newsadapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                    listOf(),
                    mainActivityViewModel
                )

                radioViewModel._radioClassicallistingArry.value = data.classical
                binding.classicalAdapter =
                    com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                        listOf(),
                        mainActivityViewModel
                    )
                mainActivityViewModel._suggesteStations.value = data.music
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE

                with(viewPager) {
                    adapter = DotIndicatorAdapter(data.podcasts)
                    setPageTransformer(true, ZoomOutPageTransformer())
                    dotsIndicator.attachTo(this)
                }
            } catch (ex: java.lang.Exception) {

                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {

                }
            }
        }

        radioViewModel.languagesListingLive.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._langListArray.value = data
                binding.languagesAdapter =
                    com.coderoids.radio.ui.radio.adapter.LanguagesAdapter(listOf(), mainActivityViewModel)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }

        radioViewModel.countriesListingLive.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._countriesListArray.value = data
                binding.countriesAdapter =
                    com.coderoids.radio.ui.radio.adapter.CountriesAdapter(listOf(), mainActivityViewModel)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }

        radioViewModel._genresListinLive.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._genresListArray.value = data
                binding.genresAdapter = com.coderoids.radio.ui.radio.adapter.GenresAdapter(listOf(), mainActivityViewModel)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }
        binding.tvAllTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value = radioViewModel.radioListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }
        binding.tvAllTagPopRock.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value = radioViewModel._radioPopListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }

        binding.tvAllTagNews.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value = radioViewModel._radioNewsListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }

        binding.tvAllTagTvClassical.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value = radioViewModel._radioClassicallistingArry.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }
    }

}