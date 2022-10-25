package com.coderoids.radio.ui.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentPodcastBinding
import com.coderoids.radio.request.Resource
import com.coderoids.radio.ui.podcast.adapter.PodcastFragmentAdapter

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
        podcastViewModel.podcastListingLive.observe(this@PodcastFragment){
            if(it != null) {
                val _data = (it as Resource.Success).value.data
                podcastViewModel._newsArrayM.value = _data.news
                binding.newsadapter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                podcastViewModel._fitnessM.value = _data.fitness
                binding.societyadapter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                podcastViewModel._businessM.value = _data.business
                binding.businessadapter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                podcastViewModel._cultureM.value = _data.culture
                binding.culturaladpter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)

                podcastViewModel._educationM.value = _data.education
                binding.educationaladapter = PodcastFragmentAdapter(listOf(), mainActivityViewModel)
            }
        }

        binding.tvAllPodcastTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._newsArrayM.value
            mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
        }
        binding.tvAllFitnessTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value =  podcastViewModel._fitnessM.value
            mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
        }
        binding.tvAllBusinessTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._businessM.value
            mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
        }
        binding.tvAllCultureTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._cultureM.value
            mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
        }

        binding.tvAllEducationalTag.setOnClickListener {
            mainActivityViewModel._selectedSeeAllPodcasts.value = podcastViewModel._educationM.value
            mainActivityViewModel._radioSeeAllSelected.value = "PODCAST"
        }


    }

}