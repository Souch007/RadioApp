package com.netcast.radio.ui.radioplayermanager

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.netcast.radio.BR
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseActivity
import com.netcast.radio.databinding.ActivityRadioPlayerBinding
import com.netcast.radio.download.DownloadActivity
import com.netcast.radio.download.DownloadUsingMediaStore
import com.netcast.radio.request.AppConstants
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class RadioPlayerActivity() :
    BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>() {
    var podcastEpisodeList: List<Data>? = null
    var podcastType: String = ""
    lateinit var radioPlayerAVM: RadioPlayerAVM
    private var STORAGE_PERMISSION_REQUEST_CODE: Int = 5049
    private var isActivityLoaded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkWifiPlaySettings()
        createActivity()
        isActivityLoaded = true
    }

    private val permissions = arrayOf(
        WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE,
        READ_MEDIA_AUDIO
    )

    private fun createActivity() {
        AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
        if (AppSingelton.suggestedRadioList != null)
            viewModel.suggestedRadioList = AppSingelton.suggestedRadioList!!
        radioPlayerAVM = viewModel
        checkWifiPlaySettings()
        //
        _checkMediaType()
        //
        Observers()
        //
        exoPlayerManager("Normal")
        //
        uiControls()
//        requestPermission()
        val result = checkPermission()
        if (!result) {
            requestPermission()
        }
        CoroutineScope(Dispatchers.IO).launch {
            getOfflineData()
        }
    }

    private fun checkWifiPlaySettings() {
        if (sharedPreferences.getBoolean("stream_over_wifi", false) && !isWifiConnected(this)) {
            Toast.makeText(
                this,
                "Please check your wifi as you enabled stream over wifi",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        if (AppSingelton._radioSelectedChannel?.value?.type == "Offline" && !isActivityLoaded) {
            createActivity()
        } else {
            AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
            if (dataBinding.podepisodeadapter != null) {
                dataBinding.podepisodeadapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun uiControls() {
        checkIfItemisInFav()
        dataBinding.ivBack.setOnClickListener {
            AppSingelton._isPlayerFragVisible.value = false
            finish()
        }
        Glide.with(dataBinding.ivChannelLogo.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.netcast.radio.R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .into(dataBinding.ivChannelLogo)

        Glide.with(dataBinding.backBlur.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.netcast.radio.R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .into(dataBinding.backBlur)
        dataBinding.channelDescription.text = AppSingelton.radioSelectedChannel?.value?.name
        dataBinding.favIv.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.addChannelToFavourites(AppSingelton.radioSelectedChannel.value!!)
            } else {
                viewModel.removeChannelFromFavourites(AppSingelton.radioSelectedChannel?.value!!)
            }
        }

    }

    private fun checkIfItemisInFav() {
        if (AppSingelton.favouritesRadioArray != null) {
            AppSingelton.favouritesRadioArray.forEachIndexed { index, playingChannelData ->
                val id = playingChannelData.id
                if (AppSingelton.radioSelectedChannel.value?.id?.toRegex()
                        ?.let { id.matches(it) } == true
                ) {
                    dataBinding.favIv.isChecked = true
                }
            }
        }

    }

    override fun onBackPressed() {
        AppSingelton._isPlayerFragVisible.value = false
        finish()
    }


    private fun exoPlayerManager(type: String) {
        if (type.matches("Episode".toRegex())) {
            dataBinding.playerView.player?.stop()
            dataBinding.playerView.player?.release()
            AppSingelton.exoPlayer = null
            dataBinding.episode.text = Html.fromHtml(AppSingelton.radioSelectedChannel.value?.name)
        }
        AppSingelton._radioSelectedChannelId = ""
        handleChannel()
    }

    private fun deletePodcast(id: String) {
        val data = getOfflineDataById(id)
        val fileUr = data.fileURI
        val fdelete: File = File(fileUr)
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                deletePodcastById(id)
            }
        }
    }

    private fun handleChannel() {
        AppSingelton._radioSelectedChannelId = AppSingelton.radioSelectedChannel.value?.id ?: ""
        val currentPlayingUUid = AppSingelton._currenPlayingChannelId
        if (AppSingelton.exoPlayer == null || !AppSingelton._radioSelectedChannelId.matches(
                currentPlayingUUid.toRegex()
            )
        ) {
            if (AppSingelton._radioSelectedChannel.value?.type?.matches("Offline".toRegex()) == true) {
                AppSingelton.exoPlayer = ExoPlayer.Builder(this)
                    .setSeekForwardIncrementMs(
                        (sharedPreferences.getLong(
                            AppConstants.PLAYER_SECS,
                            15
                        ) * 1000)
                    )
                    .setSeekBackIncrementMs(
                        (sharedPreferences.getLong(
                            AppConstants.PLAYER_SECS,
                            15
                        ) * 1000)
                    )
                    .build().also { exoPlayer ->
                        var file = File(AppSingelton._radioSelectedChannel.value!!.url)
                        if (file.exists()) {
                            dataBinding.playerView.player = exoPlayer
                            val mediaItem = MediaItem.fromUri(file.toUri())
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.addListener(this)

                        }
                    }
                Log.d("Offline", "Request Recieved")
            } else {
                val allocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)
                val loadControl = DefaultLoadControl.Builder()
                    .setAllocator(allocator)
                    .setTargetBufferBytes(C.LENGTH_UNSET)
                    .setBufferDurationsMs(10000, 120000, 1000, 1000)
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .build()
                val renderersFactory = DefaultRenderersFactory(this)
                AppSingelton.exoPlayer?.let {
                    it.release()
                    it.stop()
                }
                AppSingelton.exoPlayer =
                    ExoPlayer.Builder(this, renderersFactory)
                        .setLoadControl(loadControl)
                        .setSeekForwardIncrementMs(
                            (sharedPreferences.getLong(
                                AppConstants.PLAYER_SECS,
                                15
                            ) * 1000)
                        )
                        .setSeekBackIncrementMs(
                            (sharedPreferences.getLong(
                                AppConstants.PLAYER_SECS,
                                15
                            ) * 1000)
                        )
                        .setHandleAudioBecomingNoisy(true).build().also { exoPlayer ->
                            val url = AppSingelton.radioSelectedChannel.value?.url
                            dataBinding.playerView.player = exoPlayer
                            val mediaItem = MediaItem.fromUri(url ?: "")
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.addAnalyticsListener(object : AnalyticsListener {})
                            exoPlayer.addListener(this)

                            (dataBinding.playerView.player as ExoPlayer).prepare()
                            (dataBinding.playerView.player as ExoPlayer).play()
                        }
            }

        } else {
            dataBinding.playerView.player = AppSingelton.exoPlayer
            //dataBinding.playerView.performClick()
            dataBinding.playerView.showController()
            if (!dataBinding.playerView.isControllerVisible) {

            }
        }
    }

    private fun Observers() {
        viewModel._podEpisodesList.observe(this@RadioPlayerActivity) {
            try {
                val res = (it as Resource.Success).value
                viewModel._podEpisodeArray.value = res.data
                podcastEpisodeList = res.data
                refreshAdapter()
                dataBinding.podcastLoader.visibility = View.GONE
                if (!podcastEpisodeList!!.isNullOrEmpty()) {
                    dataBinding.playerView.visibility = View.VISIBLE
                    viewModel._episodeSelected.value = podcastEpisodeList!![0]
//                    createPlayingChannelData(podcastEpisodeList!![0])
                    dataBinding.episode.text =
                        Html.fromHtml(AppSingelton.radioSelectedChannel.value!!.name)
                } else {
//                    dataBinding.playerView.visibility = View.GONE
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

        viewModel._episodeDownloadSelected.observe(this@RadioPlayerActivity) {
            try {
                val isWifiDownloadEnable = sharedPreferences.getBoolean("download_over_wifi", false)
                if (isWifiDownloadEnable) {
                    if (isWifiConnected(this)) {
                        downloadEpisode(it)
                    } else {
                        Toast.makeText(
                            this,
                            "Your wifi is not enable if you want to download episode over data please disable download over wifi option from settings",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    downloadEpisode(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        viewModel._onepisodeDeleteSelected.observe(this@RadioPlayerActivity) {
            try {
                val id = it.id
                CoroutineScope(Dispatchers.IO).launch {
                    deletePodcast(id)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        viewModel._radioClicked.observe(this@RadioPlayerActivity) {
            try {
                exoPlayerManager("Normal")
                uiControls()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        viewModel._radioClicked.observe(this@RadioPlayerActivity) {
            try {
                exoPlayerManager("Normal")
                uiControls()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun downloadEpisode(data: Data) {
        if (AppSingelton.currentDownloading.matches("".toRegex())) {
            val data = data
            AppSingelton.downloadingEpisodeData = data
//                    DownloadFile(data).execute()
            DownloadUsingMediaStore(data, this).execute()
            startActivity(
                Intent(
                    this@RadioPlayerActivity,
                    DownloadActivity::class.java
                )
            )
//                val snackbar = Snackbar
//                    .make(dataBinding.rpLayout, "Downloading Your Podcast", Snackbar.LENGTH_LONG)
//                    .setAction("Go To Downloads ?") {
//
//                    }
//                snackbar.show()
        } else {
            Toast.makeText(
                this,
                "Please wait another download is in progress...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun refreshAdapter() {
        dataBinding.podepisodeadapter =
            com.netcast.radio.ui.radioplayermanager.adapter.PodEpisodesAdapter(
                podcastEpisodeList!!,
                viewModel
            )
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        //below android 11
        ActivityCompat.requestPermissions(
            this,
            permissions,
            STORAGE_PERMISSION_REQUEST_CODE
        )
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val READ_EXTERNAL_STORAGE = grantResults[0] === PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[1] === PackageManager.PERMISSION_GRANTED
                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                } else {
                    // Not granted
                    /* Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                         .show()*/
                }
            }
        }

    }

    private fun createPlayingChannelData(it: Data) {
        val playingChannelData = PlayingChannelData(
            it.audio,
            it.feedImage,
            it.title,
            it.id,
            AppSingelton.radioSelectedChannel.value!!.idPodcast,
            it.description,
            "Episodes"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
    }

    private fun _checkMediaType() {
        //Checking the Type of MEDIA
        if (AppSingelton.exoPlayer != null)
            Log.d("tag", AppSingelton.exoPlayer!!.audioSessionId.toString())
        podcastType = AppSingelton.radioSelectedChannel.value?.type ?: ""
        if (podcastType.matches("PODCAST".toRegex()) || podcastType.matches("Episodes".toRegex())) {
            dataBinding.podEpisodes.visibility = View.VISIBLE
            dataBinding.radioSuggestion.visibility = View.GONE
            checkPodCastEpisodes()
        } else if (podcastType.matches("Offline".toRegex())) {
            dataBinding.podEpisodes.visibility = View.GONE
            dataBinding.radioSuggestion.visibility = View.GONE
        } else {
            dataBinding.podEpisodes.visibility = View.GONE
            //changed to gone no need to display
//            dataBinding.radioSuggestion.visibility = View.GONE
            dataBinding.radioSuggestion.visibility = View.VISIBLE
            dataBinding.adapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                AppSingelton.suggestedRadioList ?: listOf(),
                viewModel
            )
        }
    }

    private fun checkPodCastEpisodes() {
        dataBinding.podcastLoader.visibility = View.VISIBLE
        viewModel.getPodcastEpisodes(AppSingelton.radioSelectedChannel.value!!.idPodcast ?: "")

    }

    override val layoutRes: Int
        get() = com.netcast.radio.R.layout.activity_radio_player
    override val bindingVariable: Int
        get() = BR.radioplayervm
    override val viewModelClass: Class<RadioPlayerAVM>
        get() = RadioPlayerAVM::class.java


}