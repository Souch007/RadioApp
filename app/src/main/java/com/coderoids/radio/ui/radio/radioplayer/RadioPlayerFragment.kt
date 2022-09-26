package com.coderoids.radio.ui.radio.radioplayer

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioPlayerBinding
import com.coderoids.radio.ui.radio.RadioViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class RadioPlayerFragment : BaseFragment<FragmentRadioPlayerBinding>(R.layout.fragment_radio_player){
    private lateinit var exoPlayer: ExoPlayer
    override fun FragmentRadioPlayerBinding.initialize() {
        binding.lifecycleOwner =this@RadioPlayerFragment
        activity.let { radioViewModel = ViewModelProvider(it!!).get(RadioViewModel::class.java) }
        exoPlayer = ExoPlayer.Builder(requireContext()).build().also { exoPlayer->
            val url = radioViewModel!!.radioClickEvent.value?.urlResolved
            binding.playButton.player = exoPlayer
            val mediaItem = MediaItem.fromUri(url!!)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.play()
        }
    }
}