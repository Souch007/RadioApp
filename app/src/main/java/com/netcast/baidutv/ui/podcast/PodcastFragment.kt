package com.netcast.baidutv.ui.podcast

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.baidutv.MainViewModel
import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseFragment
import com.netcast.baidutv.databinding.FragmentPodcastBinding
import com.netcast.baidutv.db.AppDatabase
import com.netcast.baidutv.request.Resource
import com.netcast.baidutv.ui.podcast.adapter.PodcastFragmentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastFragment : BaseFragment<FragmentPodcastBinding>(R.layout.fragment_podcast) {
    val podcastViewModel : PodcastViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel
    var appDatabase: AppDatabase? = null
    override fun FragmentPodcastBinding.initialize() {
        binding.podcastDataBinding = podcastViewModel
        appDatabase = initializeDB(requireContext())
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

                    binding.podcastLayout.visibility = View.VISIBLE
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
                            binding.podcastLayout.visibility = View.VISIBLE
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            podcastViewModel._newsArrayM.value = _data.news
                            binding.newsadapter =
                                PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                            podcastViewModel._fitnessM.value = _data.fitness
                            binding.societyadapter =
                                PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                            podcastViewModel._businessM.value = _data.business
                            binding.businessadapter =
                                PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                            podcastViewModel._cultureM.value = _data.culture
                            binding.culturaladpter =
                                PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                            podcastViewModel._educationM.value = _data.education
                            binding.educationaladapter =
                                PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                            val failure = (it as Resource.Failure).errorCode
                            val responseBody = it.errorResponseBody
                            if (failure == 400 && responseBody == null && _data==null) {
                                binding.parentView.visibility = View.GONE
                                binding.emptyViewPod.visibility = View.VISIBLE
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

    override fun onStart() {
        super.onStart()
//        mainActivityViewModel.getPodCastListing(podcastViewModel)
    }

}