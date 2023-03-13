package com.netcast.radio.ui.podcast

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentPodcastBinding
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.podcast.adapter.PodcastFragmentAdapter

class PodcastFragment : BaseFragment<FragmentPodcastBinding>(R.layout.fragment_podcast) {
    val podcastViewModel : PodcastViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentPodcastBinding.initialize() {
        binding.podcastDataBinding = podcastViewModel
//        if(podcastViewModel.podcastListArray.value !=  null && podcastViewModel.podcastListArray.value!!.size >0){
//            binding.adapter = PodcastFragmentAdapter(listOf(),podcastViewModel)
//        }
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        mainActivityViewModel.currentFragmentId = "Podcast"

        podcastViewModel.podcastListingLive.observe(this@PodcastFragment){
            if(it != null) {
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
                } catch (ex : Exception){
                    ex.printStackTrace()
                    val failure = (it as Resource.Failure).errorCode
                    val responseBody = (it as Resource.Failure).errorResponseBody
                    if (failure == 400 && responseBody == null) {
                        binding.parentView.visibility = View.GONE
                        binding.emptyViewPod.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.tvAllPodcastTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._newsArrayM.value
            mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
            mainActivityViewModel._radioSelectedTitle.value = "Politics"

        }
        binding.tvAllFitnessTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value =  podcastViewModel._fitnessM.value
            mainActivityViewModel._radioSeeAllSelected.value = "Fitness"
        }
        binding.tvAllBusinessTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._businessM.value
            mainActivityViewModel._radioSeeAllSelected.value = "Business"
        }
        binding.tvAllCultureTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._cultureM.value
            mainActivityViewModel._radioSeeAllSelected.value = "Culture"
        }

        binding.tvAllEducationalTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._educationM.value
            mainActivityViewModel._radioSeeAllSelected.value = "Education"
        }

    }

    override fun onStart() {
        super.onStart()
//        mainActivityViewModel.getPodCastListing(podcastViewModel)
    }

}