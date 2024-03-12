package com.netcast.radio.ui.podcast

import ConnectivityChecker
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentPodcastBinding
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.podcast.adapter.PodcastFragmentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class PodcastFragment : BaseFragment<FragmentPodcastBinding>(R.layout.fragment_podcast),
    ConnectivityChecker.NetworkStateListener {
    val podcastViewModel: PodcastViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    var appDatabase: AppDatabase? = null
    var isInternetavailable = true
    private lateinit var connectivityChecker: ConnectivityChecker
    override fun FragmentPodcastBinding.initialize() {
        binding.podcastDataBinding = podcastViewModel
        appDatabase = initializeDB(requireContext())
        connectivityChecker = ConnectivityChecker(requireContext())
        connectivityChecker.setListener(this@PodcastFragment)
//        if(podcastViewModel.podcastListArray.value !=  null && podcastViewModel.podcastListArray.value!!.size >0){
//            binding.adapter = PodcastFragmentAdapter(listOf(),podcastViewModel)
//        }
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        mainActivityViewModel.currentFragmentId = "Podcast"

        podcastViewModel.podcastListingLive.observe(this@PodcastFragment) {
            if (it != null) {
                try {
                    binding.parentView.visibility = View.VISIBLE
                    binding.podcastLayout.visibility = View.VISIBLE
                    binding.emptyViewPod.visibility = View.GONE
                    val _data = (it as Resource.Success).value.data
                    podcastViewModel._newsArrayM.value = _data.news
                    binding.newsadapter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                    podcastViewModel._fitnessM.value = _data.fitness
                    binding.societyadapter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                    podcastViewModel._businessM.value = _data.business
                    binding.businessadapter =
                        PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                    podcastViewModel._cultureM.value = _data.culture
                    binding.culturaladpter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                    podcastViewModel._educationM.value = _data.education
                    binding.educationaladapter =
                        PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    CoroutineScope(Dispatchers.IO).launch {
                        var data = _data
                        data.id = 0
                        appDatabase!!.appDap().insertPodCast(data)
                    }


                } catch (ex: Exception) {

                    /* binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE*/
                    CoroutineScope(Dispatchers.IO).launch {
                        val _data = appDatabase!!.appDap().getPodData()
                        withContext(Dispatchers.Main) {
                            try {
                                binding.podcastLayout.visibility = View.VISIBLE
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                                binding.emptyViewPod.visibility = View.GONE
                                podcastViewModel._newsArrayM.value = _data?.news
                                binding.newsadapter =
                                    PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                                podcastViewModel._fitnessM.value = _data?.fitness
                                binding.societyadapter =
                                    PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                                podcastViewModel._businessM.value = _data?.business
                                binding.businessadapter =
                                    PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                                podcastViewModel._cultureM.value = _data?.culture
                                binding.culturaladpter =
                                    PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                                podcastViewModel._educationM.value = _data?.education
                                binding.educationaladapter =
                                    PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                                val failure = (it as Resource.Failure).errorCode
                                val responseBody = it.errorResponseBody
                                if (failure == 400 && responseBody == null && _data == null) {
                                    binding.parentView.visibility = View.GONE
                                    binding.emptyViewPod.visibility = View.VISIBLE
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        ex.printStackTrace()

                    }
                }
            }

            binding.tvAllPodcastTag.setOnClickListener {
                mainActivityViewModel._selectedSeeAllPodcasts.value =
                    podcastViewModel._newsArrayM.value
                mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
                mainActivityViewModel._radioSelectedTitle.value = "Politics"

            }
            binding.tvAllFitnessTag.setOnClickListener {
                mainActivityViewModel._selectedSeeAllPodcasts.value =
                    podcastViewModel._fitnessM.value
                mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
                mainActivityViewModel._radioSelectedTitle.value = "Wrestling"
            }
            binding.tvAllBusinessTag.setOnClickListener {
                mainActivityViewModel._selectedSeeAllPodcasts.value =
                    podcastViewModel._businessM.value
                mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
                mainActivityViewModel._radioSelectedTitle.value = "Business"
            }
            binding.tvAllCultureTag.setOnClickListener {
                mainActivityViewModel._selectedSeeAllPodcasts.value =
                    podcastViewModel._cultureM.value
                mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
                mainActivityViewModel._radioSelectedTitle.value = "Cultural Podcasts"
            }

            binding.tvAllEducationalTag.setOnClickListener {
                mainActivityViewModel._selectedSeeAllPodcasts.value =
                    podcastViewModel._educationM.value
                mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
                mainActivityViewModel._radioSelectedTitle.value = "Educational Podcasts"
            }

        }
    }

    /*    override fun onStart() {
            super.onStart()
    //        mainActivityViewModel.getPodCastListing(podcastViewModel)
        }*/

    override fun onInternetAvailable() {
        if (!isInternetavailable) {
            if (AppSingelton.exoPlayer != null) {
                AppSingelton.exoPlayer?.playerError.takeIf { it?.sourceException is IOException }
                    ?.run {
                        AppSingelton.exoPlayer?.prepare()
                    }
            } else {
                mainActivityViewModel.getPodCastListing(podcastViewModel, "")

//                mainActivityViewModel.getPodCastListing(radioViewModel)
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

    override fun onResume() {
        super.onResume()
        if (binding.emptyViewPod.isVisible) {
            mainActivityViewModel.getPodCastListing(podcastViewModel, "")
        }
    }
}