package com.netcast.radio.ui.radio

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentRadioBinding
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.favourites.adapters.FavouriteAdapter


class RadioFragment : BaseFragment<FragmentRadioBinding>(R.layout.fragment_radio) {

    val radioViewModel: RadioViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel

    override fun FragmentRadioBinding.initialize() {
        binding.lifecycleOwner = this@RadioFragment
        binding.radioDataBinding = radioViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.shimmerLayout.stopShimmer()
        mainActivityViewModel.currentFragmentId = "Radio"
        manageRecentlyViewd()
        AppSingelton.isNewItemAdded.observe(this@RadioFragment) {
            if (it) {
                manageRecentlyViewd()
            }
        }


        radioViewModel.radioListing.observe(this@RadioFragment) {
            try {
                /*when(it){
                    is Resource.Failure -> {
//                        Toast.makeText(requireContext(),it.errorCode,Toast.LENGTH_SHORT).show()
                    }
                    Resource.Loading -> {
                        Log.d("TAG", "Loading: ")
                    }
                    is Resource.Success -> {
                        Toast.makeText(requireContext(),it.value.data.radio[0].name,Toast.LENGTH_SHORT).show()
                    }
                }
*/
                val data = (it as Resource.Success).value.data
                radioViewModel.radioListArray.value = data.publicRadio
                AppSingelton.suggestedRadioList = data.publicRadio
                binding.adapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                    listOf(),
                    mainActivityViewModel
                )

                radioViewModel._radioPopListArray.value = data.pop
                binding.popadapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                    listOf(),
                    mainActivityViewModel
                )

                radioViewModel._radioNewsListArray.value = data.news
                binding.newsadapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                    listOf(),
                    mainActivityViewModel
                )

                radioViewModel._radioClassicallistingArry.value = data.classical
                binding.classicalAdapter =
                    com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                        listOf(),
                        mainActivityViewModel
                    )
                mainActivityViewModel._suggesteStations.value = data.music
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE

//                with(viewPager) {
//                    adapter = DotIndicatorAdapter(data.podcasts,mainActivityViewModel)
//                    setPageTransformer(true, ZoomOutPageTransformer())
//                    dotsIndicator.attachTo(this)
//                }
            } catch (ex: java.lang.Exception) {

                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                val failure = (it as Resource.Failure).errorCode
                val responseBody = (it as Resource.Failure).errorResponseBody
                if (failure == 400 && responseBody == null) {
                    binding.parentView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                }
            }
        }

        radioViewModel.languagesListingLive.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._langListArray.value = data
                binding.languagesAdapter =
                    com.netcast.radio.ui.radio.adapter.LanguagesAdapter(
                        listOf(),
                        mainActivityViewModel
                    )
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }

        radioViewModel.countriesListingLive.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._countriesListArray.value = data
                binding.countriesAdapter =
                    com.netcast.radio.ui.radio.adapter.CountriesAdapter(
                        listOf(),
                        mainActivityViewModel
                    )
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }

        radioViewModel._genresListinLive.observe(this@RadioFragment) {
            try {
                val data = (it as Resource.Success).value.data
                radioViewModel._genresListArray.value = data
                binding.genresAdapter = com.netcast.radio.ui.radio.adapter.GenresAdapter(
                    listOf(),
                    mainActivityViewModel
                )
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
        binding.tvAllTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel.radioListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }
        binding.tvAllTagPopRock.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel._radioPopListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }
        binding.tvAllTagNews.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel._radioNewsListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }
        binding.tvAllTagTvClassical.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel._radioClassicallistingArry.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
        }

    }

    private fun manageRecentlyViewd() {
        if (AppSingelton.recentlyPlayedArray.size > 0) {
            binding.tvRecentlyPlayed.visibility = View.VISIBLE
            binding.recentlyPlayed.visibility = View.VISIBLE
            val recentlyPlayedAdapter =
                FavouriteAdapter(AppSingelton.recentlyPlayedArray, mainActivityViewModel)
            binding.recentlyPlayed.adapter = recentlyPlayedAdapter
        } else {
            binding.tvRecentlyPlayed.visibility = View.GONE
            binding.recentlyPlayed.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val recentlyPlayedAdapter = FavouriteAdapter(AppSingelton.recentlyPlayedArray, mainActivityViewModel)
        binding.recentlyPlayed.adapter = recentlyPlayedAdapter
    }

    override fun onStart() {
        super.onStart()
   /*     mainActivityViewModel.getRadioListing(radioViewModel)
        mainActivityViewModel.getLanguages(radioViewModel)
        mainActivityViewModel.getCountires(radioViewModel)
        mainActivityViewModel.getAllGenres(radioViewModel)*/
    }
}
