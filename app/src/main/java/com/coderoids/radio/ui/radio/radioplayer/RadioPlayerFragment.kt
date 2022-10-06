package com.coderoids.radio.ui.radio.radioplayer

import android.media.MediaPlayer
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.coderoids.radio.MainActivity
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioPlayerBinding
import com.coderoids.radio.ui.radio.RadioViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class RadioPlayerFragment : BaseFragment<FragmentRadioPlayerBinding>(R.layout.fragment_radio_player),Player.Listener{
    private lateinit var radioViewModel: RadioViewModel
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentRadioPlayerBinding.initialize() {
        binding.lifecycleOwner =this@RadioPlayerFragment
        activity.let {
            radioViewModel = ViewModelProvider(it!!).get(RadioViewModel::class.java)
            mainActivityViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
            binding.radioplayerbinding = radioViewModel
        }
        if(mainActivityViewModel.exoPlayer == null) {
            mainActivityViewModel.exoPlayer =
                ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
                    val url = binding.radioplayerbinding!!.radioClickEvent.value?.urlResolved
                    binding.playButton.player = exoPlayer
                    binding.playButton.showTimeoutMs = -1
                    val mediaItem = MediaItem.fromUri(url!!)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.addListener(this@RadioPlayerFragment)
                    binding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                        listOf(),
                        radioViewModel
                    )
                }
        } else {
            binding.playButton.player = mainActivityViewModel.exoPlayer
            binding.playButton.showTimeoutMs = -1
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._isPlayerFragVisible.value = false

        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        mainActivityViewModel._isPlayerVisible.value = isPlaying
    }

}