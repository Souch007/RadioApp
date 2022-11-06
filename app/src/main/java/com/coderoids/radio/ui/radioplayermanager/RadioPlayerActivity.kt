package com.coderoids.radio.ui.radioplayermanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.util.Log
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
import com.coderoids.radio.download.DownloadActivity
import com.coderoids.radio.download.DownloadFile
import com.coderoids.radio.request.AppConstants
import com.coderoids.radio.request.Resource
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.snackbar.Snackbar


class RadioPlayerActivity() :
    BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>() {
    var podcastEpisodeList: List<Data>? = null
    var podcastType: String = ""
    lateinit var radioPlayerAVM: RadioPlayerAVM
    var STORAGE_PERMISSION_REQUEST_CODE: Int = 5049
    var dataUrl = ""
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
        requestPermission()
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
            if (isChecked) {
                viewModel.addChannelToFavourites(AppSingelton.radioSelectedChannel.value!!)
            } else {
                viewModel.removeChannelFromFavourites(AppSingelton.radioSelectedChannel.value!!)
            }
        }

    }

    private fun exoPlayerManager(type: String) {
        if (type.matches("Episode".toRegex())) {
            dataBinding.playerView.player?.stop()
            AppSingelton.exoPlayer = null
            dataBinding.episode.text =
                Html.fromHtml(AppSingelton.radioSelectedChannel.value!!.country)
        }
        AppSingelton._radioSelectedChannelId = ""
        handleChannel()
    }

    private fun handleChannel() {
        AppSingelton._radioSelectedChannelId = AppSingelton.radioSelectedChannel.value!!.id
        var currentPlayingUUid = AppSingelton._currenPlayingChannelId
        if (AppSingelton.exoPlayer == null
            || !AppSingelton._radioSelectedChannelId.matches(currentPlayingUUid.toRegex())
        ) {
            AppSingelton.exoPlayer =
                ExoPlayer.Builder(this).build().also { exoPlayer ->
                    val url = AppSingelton.radioSelectedChannel.value?.url
                    dataBinding.playerView.player = exoPlayer
                    val mediaItem = MediaItem.fromUri(url!!)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.addListener(this)
                }
        }
        else {
            dataBinding.playerView.player = AppSingelton.exoPlayer
            //dataBinding.playerView.performClick()
            dataBinding.playerView.showController()
            if(!dataBinding.playerView.isControllerVisible){

            }
        }
    }

    private fun Observers() {
        viewModel._podEpisodesList.observe(this@RadioPlayerActivity) {
            try {
                val res = (it as Resource.Success).value
                viewModel._podEpisodeArray.value = res.data
                podcastEpisodeList = res.data
                dataBinding.podepisodeadapter =
                    com.coderoids.radio.ui.radioplayermanager.adapter.PodEpisodesAdapter(
                        podcastEpisodeList!!,
                        viewModel
                    )
                dataBinding.podcastLoader.visibility = View.GONE
                if (podcastEpisodeList != null &&
                    podcastEpisodeList!!.size > 0
                ) {
                    dataBinding.playerView.visibility = View.VISIBLE
                    createPlayingChannelData(podcastEpisodeList!!.get(0))
                    dataBinding.episode.text =
                        Html.fromHtml(AppSingelton.radioSelectedChannel.value!!.country)
                } else {
                    dataBinding.playerView.visibility = View.GONE
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
                dataBinding.podcastLoader.visibility = View.GONE
                dataBinding.episode.setText("No Episodes To Play")
            }
        }

        viewModel._episodeSelected.observe(this@RadioPlayerActivity) {
            try {
                createPlayingChannelData(it)
                exoPlayerManager("Episode")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        viewModel._episodeDownloadSelected.observe(this@RadioPlayerActivity){
            try{
                var data = it;
                val snackbar = Snackbar
                    .make(dataBinding.rpLayout, "Downloading Your Podcast", Snackbar.LENGTH_LONG)
                    .setAction("Go To Downloads ?") {
                        DownloadFile(data.enclosureUrl).execute()
                        startActivity(Intent(this@RadioPlayerActivity,DownloadActivity::class.java))
                    }
                snackbar.show()
//                    dataUrl = it.enclosureUrl
//                    var uri = Uri.parse(dataUrl)
//                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//                    val request = DownloadManager.Request(uri)
//                    request.setTitle("Downlaod Podcast")
//                    request.setDescription("Android Audio download using DownloadManager.");
//                    var relativePath = Environment.getExternalStorageDirectory().getPath() + "DownloadPodcastM"
//                    request.setDestinationInExternalFilesDir(this,relativePath,"podcast.mp3")
//                    var _id =  downloadManager.enqueue(request)

            } catch (ex : Exception){
                ex.printStackTrace()
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                // check whether storage pe rmission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            !Environment.isExternalStorageManager()
                        } else {
                            TODO("VERSION.SDK_INT < R")
                        }
                    ) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        val uri: Uri = Uri.fromParts("package", this.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {

                    }
                }
            }
            else -> {}
        }
    }

    private fun createPlayingChannelData(it: Data) {
        var playingChannelData = PlayingChannelData(
            it.enclosureUrl,
            it.feedImage,
            it.title,
            it._id.toString(),
            it.feedId.toString(),
            it.description,
            "Episodes"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
    }

    private fun _checkMediaType() {
        //Checking the Type of MEDIA
        if(AppSingelton.exoPlayer != null)
            Log.d("tag",AppSingelton.exoPlayer!!.audioSessionId.toString())
        podcastType = AppSingelton.radioSelectedChannel.value!!.type
        if (podcastType.matches("PODCAST".toRegex())) {
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
        dataBinding.podcastLoader.visibility = View.VISIBLE
        viewModel.getPodcastEpisodes(AppSingelton.radioSelectedChannel.value!!.idPodcast)

    }

    override val layoutRes: Int
        get() = R.layout.activity_radio_player
    override val bindingVariable: Int
        get() = BR.radioplayervm
    override val viewModelClass: Class<RadioPlayerAVM>
        get() = RadioPlayerAVM::class.java

}