package com.netcast.radio.ui.radio

import ConnectivityChecker
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentRadioBinding
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.favourites.adapters.FavouriteAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class RadioFragment : BaseFragment<FragmentRadioBinding>(R.layout.fragment_radio),
    ConnectivityChecker.NetworkStateListener {

    val radioViewModel: RadioViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    var appDatabase: AppDatabase? = null
    var isInternetavailable = true
    private lateinit var connectivityChecker: ConnectivityChecker
    override fun FragmentRadioBinding.initialize() {
        binding.lifecycleOwner = this@RadioFragment
        binding.radioDataBinding = radioViewModel
        appDatabase = initializeDB(requireContext())
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.shimmerLayout.stopShimmer()
        mainActivityViewModel.currentFragmentId = "Radio"
        manageRecentlyViewd()
        connectivityChecker = ConnectivityChecker(requireContext())
        connectivityChecker.setListener(this@RadioFragment)
        AppSingelton.isNewItemAdded.observe(this@RadioFragment) {
            if (it) {
                manageRecentlyViewd()
            }
        }

        radioViewModel.radioListing.observe(this@RadioFragment) {
            try {
                when (it) {
                    is Resource.Failure -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val data = appDatabase!!.appDap().getRadioData()
                            withContext(Dispatchers.Main) {
                                try {
                                    radioViewModel.radioListArray.value = data.publicRadio
                                    AppSingelton.suggestedRadioList = data.pop
                                    AppSingelton.publicList = data.pop
                                    binding.adapter =
                                        com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                            listOf(),
                                            mainActivityViewModel,
                                            "public"
                                        )

                                    radioViewModel._radioPopListArray.value = data.pop
                                    AppSingelton.popList = data?.pop
                                    binding.popadapter =
                                        com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                            listOf(),
                                            mainActivityViewModel,
                                            "pop"
                                        )

                                    radioViewModel._radioNewsListArray.value = data?.news
                                    AppSingelton.newsList = data?.news
                                    binding.newsadapter =
                                        com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                            listOf(),
                                            mainActivityViewModel,
                                            "news"
                                        )

                                    radioViewModel._radioClassicallistingArry.value =
                                        data?.classical
                                    AppSingelton.classicalList = data?.classical
                                    binding.classicalAdapter =
                                        com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                            listOf(),
                                            mainActivityViewModel,
                                            "classical"
                                        )
                                    mainActivityViewModel._suggesteStations.value = data?.music
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    binding.parentView.visibility = View.GONE
                                    binding.emptyView.visibility = View.VISIBLE
                                }
                            }
                            /* if (data.publicRadio.isEmpty()) {
                                 binding.tvAllTag.visibility=View.GONE
                                 binding.
                                 binding.allRadioStations.visibility = View.GONE
                             }*/

                        }


                    }

                    Resource.Loading -> {
                        //Log("TAG", "Loading: ")
                    }

                    is Resource.Success -> {
                        binding.parentView.visibility = View.VISIBLE
                        val data = it.value.data
                        radioViewModel.radioListArray.value = data.publicRadio
                        radioViewModel.radioListArray.value = data.publicRadio
                        AppSingelton.suggestedRadioList = data.pop
                        AppSingelton.publicList = data.pop
                        binding.adapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                            listOf(),
                            mainActivityViewModel,
                            "public"
                        )

                        radioViewModel._radioPopListArray.value = data.pop
                        AppSingelton.popList = data.pop
                        binding.popadapter =
                            com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                listOf(),
                                mainActivityViewModel,
                                "pop"
                            )

                        radioViewModel._radioNewsListArray.value = data.news
                        AppSingelton.newsList = data.news
                        binding.newsadapter =
                            com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                listOf(),
                                mainActivityViewModel,
                                "news"
                            )

                        radioViewModel._radioClassicallistingArry.value = data.classical
                        AppSingelton.classicalList = data.classical
                        binding.classicalAdapter =
                            com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                                listOf(),
                                mainActivityViewModel,
                                "classical"
                            )
                        mainActivityViewModel._suggesteStations.value = data.music
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        CoroutineScope(Dispatchers.IO).launch {
                            var data = it.value.data
                            data.id = 0
                            appDatabase!!.appDap().insertRadioStations(data)

                        }
                    }
                }


//                with(viewPager) {
//                    adapter = DotIndicatorAdapter(data.podcasts,mainActivityViewModel)
//                    setPageTransformer(true, ZoomOutPageTransformer())
//                    dotsIndicator.attachTo(this)
//                }
            } catch (ex: java.lang.Exception) {
                //Log("TAG", "initialize: ${ex.message}")
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

        radioViewModel.languagesListingLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                val sortedList = data.sortedBy { it.name }
                radioViewModel._langListArray.value = sortedList

                binding.languagesAdapter =
                    com.netcast.radio.ui.radio.adapter.LanguagesAdapter(
                        listOf(),
                        mainActivityViewModel,
                        "limited"
                    )
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }

        radioViewModel.countriesListingLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                val sortedList = data.sortedBy { it.name }
                radioViewModel._countriesListArray.value = sortedList
                binding.countriesAdapter =
                    com.netcast.radio.ui.radio.adapter.CountriesAdapter(
                        listOf(),
                        mainActivityViewModel,
                        "limited"
                    )
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }

        radioViewModel._genresListinLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                val sortedList = data.sortedBy { it.name }
                radioViewModel._genresListArray.value = sortedList
                binding.genresAdapter = com.netcast.radio.ui.radio.adapter.GenresAdapter(
                    listOf(),
                    mainActivityViewModel,
                    "few"

                )
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
        binding.tvAllTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel.radioListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
            mainActivityViewModel._radioSelectedTitle.value = "Local Stations"
        }
        binding.tvAllTagPopRock.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel._radioPopListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
            mainActivityViewModel._radioSelectedTitle.value = "Most Listened Pop and Rock"
        }
        binding.tvAllTagNews.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel._radioNewsListArray.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
            mainActivityViewModel._radioSelectedTitle.value = "News and Culture"
        }
        binding.tvAllTagTvClassical.setOnClickListener {
            mainActivityViewModel._selectedSeeAllListRadio.value =
                radioViewModel._radioClassicallistingArry.value
            mainActivityViewModel._radioSeeAllSelected.value = "RADIO"
            mainActivityViewModel._radioSelectedTitle.value = "Classical"
        }
        binding.tvAllTagTvGenres.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_radio_to_allGenreFragment
            )
        }
        binding.tvAllTagTvCountries.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_radio_to_allcountriesFragment)
        }
        binding.tvAllTagTvLanguages.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_radio_to_allLanguagesFragment)
        }

        binding.tvAllRecentplayed.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_radio_to_allRecentlyPlayedFragment)
        }

    }

    private fun manageRecentlyViewd() {
        if (AppSingelton.recentlyPlayedArray.size > 0) {
            binding.tvRecentlyPlayed.visibility = View.VISIBLE
            binding.recentlyPlayed.visibility = View.VISIBLE
            val recentlyPlayedAdapter =
                FavouriteAdapter(
                    AppSingelton.recentlyPlayedArray.asReversed(),
                    mainActivityViewModel,
                    "recently_played"
                )
            binding.recentlyPlayed.adapter = recentlyPlayedAdapter
        } else {
            binding.tvRecentlyPlayed.visibility = View.GONE
            binding.recentlyPlayed.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        try {
            val recentlyPlayedAdapter = AppSingelton?.recentlyPlayedArray?.asReversed()
                ?.let { FavouriteAdapter(it, mainActivityViewModel, "recently_played") }
            binding.recentlyPlayed.adapter = recentlyPlayedAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onInternetAvailable() {
        if (!isInternetavailable) {
            if (AppSingelton.exoPlayer != null) {
                AppSingelton.exoPlayer?.playerError.takeIf { it?.sourceException is IOException }
                    ?.run {
                        AppSingelton.exoPlayer?.prepare()
                    }
            } else {
                mainActivityViewModel.getRadioListing(radioViewModel, "")
                mainActivityViewModel.getLanguages(radioViewModel)
                mainActivityViewModel.getCountires(radioViewModel)
                mainActivityViewModel.getAllGenres(radioViewModel)
            }
        }
        isInternetavailable = true
    }

    override fun onInternetUnavailable() {
        isInternetavailable = false
        showToast("No internet connection please check your internet connection")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        connectivityChecker.register()
    }

    override fun onStop() {
        super.onStop()
        connectivityChecker.unregister()
    }

}
