package com.coderoids.radio.ui.radio.radioplayer

import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioPlayerBinding
import com.coderoids.radio.ui.radio.RadioViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class RadioPlayerFragment : BaseFragment<FragmentRadioPlayerBinding>(R.layout.fragment_radio_player){
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var radioViewModel: RadioViewModel
    override fun FragmentRadioPlayerBinding.initialize() {
        binding.lifecycleOwner =this@RadioPlayerFragment
        activity.let {
            radioViewModel = ViewModelProvider(it!!).get(RadioViewModel::class.java)
            binding.radioplayerbinding = radioViewModel
        }
        exoPlayer = ExoPlayer.Builder(requireContext()).build().also { exoPlayer->
            val url = binding.radioplayerbinding!!.radioClickEvent.value?.urlResolved
            binding.playButton.player = exoPlayer
            val mediaItem = MediaItem.fromUri(url!!)
            exoPlayer.setMediaItem(mediaItem)
            binding.playButton.setOnClickListener {
                exoPlayer.play()
            }
            binding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(listOf(),radioViewModel)
        }
    }
}