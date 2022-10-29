package com.coderoids.radio.ui.radioplayermanager

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coderoids.radio.BR
import com.coderoids.radio.PlayingChannelData
import com.coderoids.radio.R
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseActivity
import com.coderoids.radio.databinding.ActivityRadioPlayerBinding
import com.coderoids.radio.request.AppConstants
import com.coderoids.radio.request.Resource
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class RadioPlayerActivity(
) : BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>() {
    lateinit var radioPlayerAVM : RadioPlayerAVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
        viewModel.suggestedRadioList = AppSingelton.suggestedRadioList!!
        radioPlayerAVM = viewModel
        //
        _checkMediaType()
        //
        Observers()
        //
        exoPlayerManager("Normal")
        //
        uiControls()
    }

    private fun uiControls() {
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

    private fun exoPlayerManager(type: String) {
        if(type.matches("Episode".toRegex())){
            dataBinding.playButton.player?.stop()
            AppSingelton.exoPlayer = null
        }
        AppSingelton._radioSelectedChannelId = AppSingelton.radioSelectedChannel.value!!.id
        var currentPlayingUUid =  AppSingelton._currenPlayingChannelId
        if(AppSingelton.exoPlayer == null
            || !AppSingelton._radioSelectedChannelId.matches(currentPlayingUUid.toRegex())) {
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
    }

    private fun Observers() {
        viewModel._podEpisodesList.observe(this@RadioPlayerActivity){
            try {
                val res = (it as Resource.Success).value
                viewModel._podEpisodeArray.value = res.data
                var list = res.data
                dataBinding.podepisodeadapter = com.coderoids.radio.ui.radioplayermanager.adapter.PodEpisodesAdapter(
                    list ,
                    viewModel
                )
            } catch (ex : Exception){
                ex.printStackTrace()
            }
        }

        viewModel._episodeSelected.observe(this@RadioPlayerActivity){
            try{
                var playingChannelData = PlayingChannelData(it.enclosureUrl,
                    it.feedImage,it.title,it._id.toString(),it.feedId.toString(),"","Episodes")
                AppSingelton._radioSelectedChannel.value = playingChannelData
                exoPlayerManager("Episode")
            } catch (ex: Exception){
                ex.printStackTrace()
            }
        }
    }

    private fun _checkMediaType() {
        //Checking the Type of MEDIA
        if(AppSingelton.radioSelectedChannel.value!!.type.matches("PODCAST".toRegex())){
            dataBinding.podEpisodes.visibility = View.VISIBLE
            dataBinding.radioSuggestion.visibility = View.GONE
            checkPodCastEpisodes()
        } else {
            dataBinding.podEpisodes.visibility = View.GONE
            dataBinding.radioSuggestion.visibility = View.VISIBLE
            dataBinding.adapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                AppSingelton.suggestedRadioList!!,
                viewModel
            )
        }
    }

    private fun checkPodCastEpisodes() {
        viewModel.getPodcastEpisodes(AppSingelton.radioSelectedChannel.value!!.idPodcast)
    }

    override val layoutRes: Int
        get() = R.layout.activity_radio_player
    override val bindingVariable: Int
        get() = BR.radioplayervm
    override val viewModelClass: Class<RadioPlayerAVM>
        get() = RadioPlayerAVM::class.java

}