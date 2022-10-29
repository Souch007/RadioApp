package com.coderoids.radio.ui.radioplayermanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aemerse.slider.utils.setImage
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coderoids.radio.BR
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseActivity
import com.coderoids.radio.base.BaseViewModel
import com.coderoids.radio.databinding.ActivityRadioPlayerBinding
import com.coderoids.radio.request.AppConstants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class RadioPlayerActivity(
) : BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.suggestedRadioList = AppSingelton.suggestedRadioList!!
        dataBinding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                AppSingelton.suggestedRadioList!!,
                viewModel
            )
        AppSingelton._radioSelectedChannelId = AppSingelton.radioSelectedChannel.value!!.id
        var currentPlayingUUid =  AppSingelton._currenPlayingChannelId
        if(AppSingelton.exoPlayer == null || !AppSingelton._radioSelectedChannelId.matches(currentPlayingUUid.toRegex())) {
            AppSingelton.exoPlayer =
                ExoPlayer.Builder(this).build().also { exoPlayer ->
                    val url = AppSingelton.radioSelectedChannel.value?.url
                    dataBinding.playButton.player = exoPlayer
                    dataBinding.playButton.showTimeoutMs = -1
                    val mediaItem = MediaItem.fromUri(url!!)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.addListener(this)
                }
        } else {
            dataBinding.playButton.player = AppSingelton.exoPlayer
            dataBinding.playButton.showTimeoutMs = -1
        }
        dataBinding.ivBack.setOnClickListener {
            AppSingelton._isPlayerFragVisible.value = false
            finish()
        }
        Glide.with(dataBinding.ivChannelLogo.context)
            .load(AppSingelton.radioSelectedChannel.value!!.favicon)
            .error(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .into(dataBinding.ivChannelLogo)

        Glide.with(dataBinding.backBlur.context)
            .load(AppSingelton.radioSelectedChannel.value!!.favicon)
            .error(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .into(dataBinding.backBlur)
        dataBinding.channelDescription.text = AppSingelton.radioSelectedChannel.value!!.name
        dataBinding.favIv.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                viewModel.addChannelToFavourites(AppSingelton.radioSelectedChannel.value!!)
            } else  {
                viewModel.removeChannelFromFavourites(AppSingelton.radioSelectedChannel.value!!)
            }
        }
    }

    override val layoutRes: Int
        get() = R.layout.activity_radio_player
    override val bindingVariable: Int
        get() = BR.radioplayervm
    override val viewModelClass: Class<RadioPlayerAVM>
        get() = RadioPlayerAVM::class.java

}