package com.coderoids.radio.ui.radio.radioplayer

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentRadioPlayerBinding
import com.coderoids.radio.ui.radio.RadioViewModel

class RadioPlayerFragment : BaseFragment<FragmentRadioPlayerBinding>(R.layout.fragment_radio_player){
    //val radioViewModel : RadioViewModel by navGraphViewModels(R.id.nav_host_fragment_activity_main)
    private lateinit var radioViewModel: RadioViewModel

    override fun FragmentRadioPlayerBinding.initialize() {
        binding.lifecycleOwner =this@RadioPlayerFragment
        activity.let {
            radioViewModel = ViewModelProvider(it!!).get(RadioViewModel::class.java)

        }
        binding.playButton.setOnClickListener {
            val url = radioViewModel!!.radioClickEvent.value?.urlResolved
            val mediaPlayer : MediaPlayer =  MediaPlayer()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.start()
        }


    }

}