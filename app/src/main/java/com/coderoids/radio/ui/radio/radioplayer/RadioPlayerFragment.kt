package com.coderoids.radio.ui.radio.radioplayer

import android.content.res.ColorStateList
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
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
            binding.mainviewmodel = mainActivityViewModel
        }
        mainActivityViewModel.currentFragmentId = "RadioPlayer"

        val selectedUUid : String= mainActivityViewModel._radioSelectedChannel.value!!.id
        var currentPlayingUUid : String = "0"
        if(mainActivityViewModel._currentPlayingChannel.value != null)
            currentPlayingUUid = mainActivityViewModel._currentPlayingChannel.value!!.id
        if(mainActivityViewModel.exoPlayer == null || !selectedUUid.matches(currentPlayingUUid.toRegex())) {
            mainActivityViewModel.exoPlayer =
                ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
                    val url = mainActivityViewModel.radioSelectedChannel.value?.url
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


        binding.favIv.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mainActivityViewModel.addChannelToFavourites(mainActivityViewModel.radioSelectedChannel.value!!)
            } else  {
                mainActivityViewModel.removeChannelFromFavourites(mainActivityViewModel._radioSelectedChannel.value!!)
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        mainActivityViewModel._currentPlayingChannel = mainActivityViewModel._radioSelectedChannel
        mainActivityViewModel._isStationActive.value = isPlaying
        try {
            if(binding != null && binding.animationView != null) {
                if (isPlaying)
                    binding.animationView.visibility = View.VISIBLE
                else
                    binding.animationView.visibility = View.GONE
            }
        } catch (ex : Exception){
            ex.printStackTrace()
        }


    }

}