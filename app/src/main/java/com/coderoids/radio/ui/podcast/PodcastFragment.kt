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

class PodcastFragment : BaseFragment<FragmentPodcastBinding>(R.layout.fragment_podcast) {
    val podcastViewModel : PodcastViewModel by activityViewModels()

    override fun FragmentPodcastBinding.initialize() {
        binding.podcastViewModel = podcastViewModel
    }

}