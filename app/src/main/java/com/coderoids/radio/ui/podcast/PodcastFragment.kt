package com.coderoids.radio.ui.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentPodcastBinding
import com.coderoids.radio.request.Resource
import com.coderoids.radio.ui.podcast.adapter.PodcastFragmentAdapter

class PodcastFragment : BaseFragment<FragmentPodcastBinding>(R.layout.fragment_podcast) {
    val podcastViewModel : PodcastViewModel by activityViewModels()

    override fun FragmentPodcastBinding.initialize() {
        binding.podcastDataBinding = podcastViewModel
        podcastViewModel.getPodcastListing()

        podcastViewModel.podcastListingLive.observe(this@PodcastFragment){
            val data = (it as Resource.Success).value.feeds
            podcastViewModel._podcastListArray.value = data
            binding.adapter = PodcastFragmentAdapter(listOf(),podcastViewModel)
        }

    }

}