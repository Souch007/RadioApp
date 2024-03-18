package com.netcast.radio.ui.radioplayermanager

import ConnectivityChecker
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.media.session.PlaybackStateCompat
import android.telephony.TelephonyManager
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.netcast.radio.BR
import com.netcast.radio.MainViewModel
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseActivity
import com.netcast.radio.databinding.ActivityRadioPlayerBinding
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.interfaces.OnDialogClose
import com.netcast.radio.request.AppConstants
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.netcast.radio.ui.radioplayermanager.adapter.PodEpisodesAdapter
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import com.netcast.radio.ui.ui.settings.AlarmFragment
import com.netcast.radio.ui.ui.settings.SleepTimerFragment
import com.netcast.radio.util.AlternateChannelsDialog
import com.netcast.radio.util.BottomSheetOptionsFragment
import com.netcast.radio.util.ConnectivityHandler
import com.netcast.radio.util.OptionsClickListner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


class RadioPlayerActivity() : BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>(),
    OptionsClickListner, ConnectivityChecker.NetworkStateListener, OnDialogClose {
    var podcastEpisodeList: List<Data>? = null
    var podcastType: String = ""
    private lateinit var radioPlayerAVM: RadioPlayerAVM
    private lateinit var mainViewModel: MainViewModel
    private var STORAGE_PERMISSION_REQUEST_CODE: Int = 5049
    private var isActivityLoaded = false
    var relativePath = ""
    private var downloadManager: DownloadManager? = null
    lateinit var podEpisodesAdapter: PodEpisodesAdapter
    lateinit var moreradioAdapter: com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter
    var isEpisode = false
    var playwhenReady = false
    var isInternetavailable = true
    var currentindex = 0
    private var customDialog: AlternateChannelsDialog? = null
    private val permissions = arrayOf(
        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, READ_MEDIA_AUDIO
    )
    private lateinit var connectivityChecker: ConnectivityChecker
    private var count = 0

    private lateinit var connectivityHandler: ConnectivityHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkWifiPlaySettings()) {
            createActivity()
            isActivityLoaded = true
        } else {
            showToast("Please check your wifi as you enabled stream over wifi")
            finish()
        }
        connectivityHandler = ConnectivityHandler(this)
        connectivityChecker = ConnectivityChecker(this)
        connectivityChecker.setListener(this)
        dataBinding.imgClose.setOnClickListener {
            AppSingelton._isPlayerFragVisible.value = false
            finish()
        }
        dataBinding.btnPlaynext.setOnClickListener {
            currentindex += 1
            var nextChanneltoPlay = AppSingelton.suggestedRadioList?.get(
                AppSingelton.suggestedRadioList?.size?.minus(currentindex) ?: 0
            )

            AppSingelton._radioSelectedChannel.value = nextChanneltoPlay?.id?.let { id ->
                PlayingChannelData(
                    nextChanneltoPlay?.url,
                    nextChanneltoPlay?.favicon,
                    nextChanneltoPlay?.name,
                    id,
                    "",
                    nextChanneltoPlay?.country,
                    "RADIO",
                    secondaryUrl = nextChanneltoPlay.secondaryUrl,
                    isBlocked = nextChanneltoPlay.isBlocked,
                    description = nextChanneltoPlay.description
                )
            }/* if (nextChanneltoPlay?.isBlocked == true)
                 dataBinding.llBlock.visibility = View.VISIBLE
             else {*/
//            dataBinding.llBlock.visibility = View.GONE
            dataBinding.progressDownload.visibility = View.VISIBLE
            AppSingelton.exoPlayer = null
//            createActivity()

        }
    }

    private fun createActivity() {

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
        if (AppSingelton.suggestedRadioList != null) viewModel.suggestedRadioList =
            AppSingelton.suggestedRadioList!!
        radioPlayerAVM = viewModel
        checkWifiPlaySettings()
        Observers()
        _checkMediaType()/* exoPlayerManager("Normal")
         uiControls()*/
//        requestPermission()
        val result = checkPermission()
        if (!result) {
            requestPermission()
        }
        getofflinedata()

        dataBinding.icPlay.setOnClickListener {
            if (dataBinding.playerView.player?.isPlaying == true) {
                dataBinding.playerView.player?.pause()
                dataBinding.icPlay.setImageResource(com.netcast.radio.R.drawable.play_button)
            } else {
                dataBinding.playerView.player?.play()
                dataBinding.icPlay.setImageResource(com.netcast.radio.R.drawable.pause_button)
            }

        }
        dataBinding.playerOptions.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun getofflinedata() {
        CoroutineScope(Dispatchers.IO).launch {
            getOfflineData()
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetFragment = BottomSheetOptionsFragment(this, true, isEpisode)
        bottomSheetFragment.show(supportFragmentManager, "BSDialogFragment")

    }

    private fun checkWifiPlaySettings(): Boolean {
        val iswifiplay = sharedPreferences.getBoolean("stream_over_wifi", false)
        return iswifiplay && !isWifiConnected(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        connectivityHandler.startCheckingConnectivity()
        if (AppSingelton._radioSelectedChannel?.value?.type == "Offline" && !isActivityLoaded) {
            createActivity()
        } else {
            AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
            if (dataBinding.podepisodeadapter != null) {
                dataBinding.podepisodeadapter!!.notifyDataSetChanged()
            }
        }
        mainViewModel.getalternateChannels()
    }


    private fun uiControls() {
        checkIfItemisInFav()
        dataBinding.ivBack.setOnClickListener {
            AppSingelton._isPlayerFragVisible.value = false
            finish()
        }
        Glide.with(dataBinding.ivChannelLogo.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.netcast.radio.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(dataBinding.ivChannelLogo)

        Glide.with(dataBinding.imageView.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.netcast.radio.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(dataBinding.imageView)

        Glide.with(dataBinding.backBlur.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.netcast.radio.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(dataBinding.backBlur)
        dataBinding.channelDescription.text = HtmlCompat.fromHtml(
            AppSingelton.radioSelectedChannel?.value?.description ?: "",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )


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
            AppSingelton.exoPlayer?.stop()
            AppSingelton.exoPlayer?.release()
            AppSingelton.exoPlayer = null
            dataBinding.episode.text = Html.fromHtml(AppSingelton.radioSelectedChannel.value?.name)
        }
        AppSingelton._radioSelectedChannelId = ""
        handleChannel()
        dataBinding.playerView.player?.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                playwhenReady = playWhenReady
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    dataBinding.icPlay.setImageResource(com.netcast.radio.R.drawable.pause_button)
                    dataBinding.progressDownload.visibility = View.INVISIBLE
                    AppSingelton._erroPlayingChannel.postValue("")
                    if (AppSingelton.radioSelectedChannel.value?.isBlocked == true) {
                        AppSingelton.radioSelectedChannel.value?.id?.let { it1 ->
                            radioPlayerAVM.unblockStation(
                                it1
                            )
                        }
                    }
                } else if (playbackState == PlaybackStateCompat.STATE_STOPPED) {
                    AppSingelton._erroPlayingChannel.postValue("PlayerStopped")
//                    showToast("Stopped")
                    dataBinding.icPlay.setImageResource(com.netcast.radio.R.drawable.play_button)
                }
            }


            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                dataBinding.progressDownload.visibility = View.INVISIBLE
                dataBinding.playerView.player?.stop()
            }

        })
    }

    override fun deletePodcast(id: String) {
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
        try {
            val isAutoPlayEnable =
                sharedPreferences.getBoolean(AppConstants.AUTO_PLAY_EPISODES, false)
            AppSingelton._radioSelectedChannelId = AppSingelton.radioSelectedChannel.value?.id ?: ""
            val currentPlayingUUid = AppSingelton._currenPlayingChannelId
            if (AppSingelton.exoPlayer == null || !AppSingelton._radioSelectedChannelId.matches(
                    currentPlayingUUid.toRegex()
                )
            ) {
                if (AppSingelton._radioSelectedChannel.value?.type?.matches("Offline".toRegex()) == true) {
                    AppSingelton.exoPlayer = ExoPlayer.Builder(this).setSeekForwardIncrementMs(
                        (sharedPreferences.getLong(
                            AppConstants.PLAYER_SECS, 15
                        ) * 1000)
                    ).setSeekBackIncrementMs(
                        (sharedPreferences.getLong(
                            AppConstants.PLAYER_SECS, 15
                        ) * 1000)
                    ).setPauseAtEndOfMediaItems(
                        sharedPreferences.getBoolean(
                            AppConstants.SKIP_SLIENCE, false
                        )
                    ).build().also { exoPlayer ->
                        var file = File(AppSingelton._radioSelectedChannel.value!!.url)
                        if (file.exists()) {
                            dataBinding.playerView.player = exoPlayer


                            val filePath =
                                if (count == 0 && AppSingelton._radioSelectedChannel.value!!.secondaryUrl.isNotEmpty()) AppSingelton._radioSelectedChannel.value!!.secondaryUrl else AppSingelton._radioSelectedChannel.value!!.url
                            val uri: Uri = Uri.parse(filePath)
                            // Prepare media item and start playback
                            val mediaItem = MediaItem.fromUri(uri)
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.play()
                            exoPlayer.addListener(this)

                        }
                    }
                    //Log("Offline", "Request Recieved")
                } else {
//                val allocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)
                    val allocator = DefaultAllocator(true, 64 * 1024)
                    val loadControl = DefaultLoadControl.Builder().setAllocator(allocator)
                        .setTargetBufferBytes(C.LENGTH_UNSET)
//                        .setBufferDurationsMs(60000, 36000000, 1000, 1000)
                        .setBufferDurationsMs(60000, 3600000, 2500, 5000)
                        .setPrioritizeTimeOverSizeThresholds(true).build()
                    val renderersFactory = DefaultRenderersFactory(this)
                    AppSingelton.exoPlayer?.let {
                        it.release()
                        it.stop()
                    }
                    AppSingelton.exoPlayer =
                        ExoPlayer.Builder(this, renderersFactory).setLoadControl(loadControl)
                            .setSeekForwardIncrementMs(
                                (sharedPreferences.getLong(
                                    AppConstants.PLAYER_SECS, 15
                                ) * 1000)
                            ).setSeekBackIncrementMs(
                                (sharedPreferences.getLong(
                                    AppConstants.PLAYER_SECS, 15
                                ) * 1000)
                            ).setHandleAudioBecomingNoisy(true).build().also { exoPlayer ->
                                val currentChannel = AppSingelton.radioSelectedChannel.value
//                                showToast(AppSingelton.radioSelectedChannel?.value?.name!!)
                                dataBinding.playerView.player = exoPlayer

                                if (isAutoPlayEnable && (podcastType.matches("PODCAST".toRegex()) || podcastType.matches(
                                        "Episodes".toRegex()
                                    )) && !podcastEpisodeList.isNullOrEmpty()
                                ) {
                                    val mediaitems = mutableListOf<MediaItem>()
                                    for (i in 0 until podcastEpisodeList!!.size) {
                                        val mediaMetadata = MediaMetadata.Builder()
                                            .setTitle(podcastEpisodeList!![i].title)
                                            .setDescription(podcastEpisodeList!![i].description)
                                            .setArtworkUri(Uri.parse(podcastEpisodeList!![i].thumbnail))
                                            .build()
                                        val mediaItem: MediaItem = MediaItem.Builder()
                                            .setUri(podcastEpisodeList!![i].audio.toUri())
                                            .setMediaMetadata(mediaMetadata)
                                            .setMediaId(i.toString()).setTag(i).build()
                                        mediaitems.add(mediaItem)
//                                mediaitems.add(MediaItem.fromUri(podcastEpisodeList!![i].audio))
                                    }

                                    AppSingelton.mediaItemList = mediaitems
                                    exoPlayer.setMediaItems(mediaitems)
                                } else {
                                    val mediaitemschannels = mutableListOf<MediaItem>()
                                    val list = AppSingelton.suggestedRadioList?.toMutableList()
                                    val selectedRadio = RadioLists(
                                        currentChannel?.country ?: "",
                                        currentChannel?.favicon ?: "",
                                        currentChannel?.id ?: "",
                                        currentChannel?.name ?: "",
                                        currentChannel?.url ?: "",
                                        currentChannel?.secondaryUrl ?: "",
                                        false,
                                        description = currentChannel?.description ?: ""

                                    )

                                    list?.add(0, selectedRadio)
//                                AppSingelton.selectedChannel?.let { list?.add(0, it) }
                                    if (!list?.contains(selectedRadio)!!) list?.add(
                                        0, selectedRadio
                                    )

                                    val distinctList = list.distinctBy {
                                        Pair(
                                            it.name, it.name
                                        )
                                    }
                                    AppSingelton.suggestedRadioList = distinctList

                                    for (i in 0 until AppSingelton.suggestedRadioList!!.size) {

                                        val mediaMetadata = MediaMetadata.Builder()
                                            .setTitle(AppSingelton.suggestedRadioList!![i].name)
                                            .setDescription(AppSingelton.suggestedRadioList!![i].country)
                                            .setArtworkUri(Uri.parse(AppSingelton.suggestedRadioList!![i].favicon))
                                            .build()
                                        val mediaItem: MediaItem = MediaItem.Builder()
                                            .setUri(getUrl((AppSingelton.suggestedRadioList as MutableList<RadioLists>)[i]))
                                            .setMediaMetadata(mediaMetadata)
                                            .setMediaId(i.toString()).setTag(i).build()
                                        mediaitemschannels.add(mediaItem)
//                                mediaitems.add(MediaItem.fromUri(podcastEpisodeList!![i].audio))
                                    }
                                    AppSingelton.mediaItemList = mediaitemschannels
                                    exoPlayer.setMediaItems(mediaitemschannels)
                                }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUrl(suggestedRadioList: RadioLists): String {
        return if (suggestedRadioList?.secondaryUrl?.isNotEmpty() == true && count == 1) suggestedRadioList.secondaryUrl
        else suggestedRadioList.url
    }

    private fun Observers() {
        viewModel._podEpisodesList.observe(this@RadioPlayerActivity) {
            try {
                val res = (it as Resource.Success).value
                viewModel._podEpisodeArray.value = res.data
                podcastEpisodeList = res.data
                dataBinding.podcastLoader.visibility = View.GONE
                if (!podcastEpisodeList!!.isNullOrEmpty()) {
//                    dataBinding.playerView.visibility = View.VISIBLE

                    refreshAdapter()
                    viewModel._episodeSelected.value = podcastEpisodeList!![0]

                    dataBinding.episode.text =
                        Html.fromHtml(AppSingelton.radioSelectedChannel.value!!.name)
                } else {
//                    dataBinding.playerView.visibility = View.GONE
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
                dataBinding.podcastLoader.visibility = View.GONE
                dataBinding.episode.text = "No Episodes To Play"
            }
        }

        viewModel._episodeSelected.observe(this@RadioPlayerActivity) {
            try {
                isEpisode = true
                createPlayingChannelData(it)
                exoPlayerManager("Episode")
                viewModel.setStatitcs(
                    it.title!!,
                    it.id,
                    "PODCAST",
                    getCountryCode(this@RadioPlayerActivity),
                    getDeviceId()
                )
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
//                    podEpisodesAdapter.downloadEpisode(it)
                    } else {
                        showToast("Your wifi is not enable if you want to download episode over data please disable download over wifi option from settings")
                    }

                } else {
                    downloadEpisode(it)
//                podEpisodesAdapter.downloadEpisode(it)
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
        viewModel._onepisodeShareClicked.observe(this@RadioPlayerActivity) {
            try {
//                share("Checkout this link its amazing. ", it.listennotesUrl)

                AppConstants.share(
                    "Checkout this link its amazing. ",
                    AppSingelton._radioSelectedChannel.value,
                    this
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /*   viewModel._radioClicked.observe(this@RadioPlayerActivity) {
               count=0
               dataBinding.tvChannelName.text = it.name
               dataBinding.llBlock.visibility = View.GONE
               dataBinding.progressDownload.visibility = View.VISIBLE
               customDialog?.dismiss()
               try {
                   if (AppSingelton.exoPlayer != null) {
                       AppSingelton.exoPlayer!!.stop()
                       AppSingelton.exoPlayer!!.release()
                   }
                   AppSingelton.exoPlayer = null


                   exoPlayerManager("Normal")
                   uiControls()
               } catch (ex: Exception) {
                   ex.printStackTrace()
               }
           }
   */

        AppSingelton.errorPlayingChannel.observe(this) {
            if (it.isNotEmpty() && podcastType != "PODCAST" && podcastType != "Episodes" && isInternetavailable) {
                if (count == 1) {
//                    dataBinding.llBlock.visibility = View.VISIBLE
                    customDialog = viewModel.alternateChannels?.let { it1 ->
                        AlternateChannelsDialog(
                            this, it1, mainViewModel, this
                        )
                    }
                    customDialog?.show()
                    AppSingelton._erroPlayingChannel.postValue("")
                    AppSingelton.radioSelectedChannel.value?.id?.let { it1 ->
                        radioPlayerAVM.blockStation(
                            it1
                        )
                    }
                } else {
                    count += 1
                    dataBinding.progressDownload.visibility = View.VISIBLE
                    if (AppSingelton.exoPlayer != null) {
                        AppSingelton.exoPlayer!!.stop()
                        AppSingelton.exoPlayer!!.release()
                    }
                    AppSingelton.exoPlayer = null
                    if (!isEpisode) exoPlayerManager("Normal")
                    else exoPlayerManager("Episode")
                }
            } else {
//                dataBinding.progressDownload.visibility = View.INVISIBLE
//                showToast("Please check your internet connection")
            }
        }
        mainViewModel._alternateChannels.observe(this) {
            when (it) {
                is Resource.Failure -> {}
                is Resource.Loading -> {}
                is Resource.Success -> {
                    viewModel.alternateChannels = it.value.all
                    val newalternatives =
                        viewModel.alternateChannels?.filter { it.name != AppSingelton.radioSelectedChannel.value?.name && !it.isBlocked }
                    moreradioAdapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
                        newalternatives ?: listOf(), viewModel, "public"
                    )
                    dataBinding.adapter = moreradioAdapter
                }
            }
        }
        AppSingelton.radioSelectedChannel.observe(this) {
            it?.let {
                count = 0
                dataBinding.tvChannelName.text = it.name
                dataBinding.llBlock.visibility = View.GONE
                dataBinding.progressDownload.visibility = View.VISIBLE
                customDialog?.dismiss()
                try {
                    if (AppSingelton.exoPlayer != null) {
                        AppSingelton.exoPlayer!!.stop()
                        AppSingelton.exoPlayer!!.release()
                        (dataBinding.playerView.player as ExoPlayer).stop()
                        (dataBinding.playerView.player as ExoPlayer).release()
                        AppSingelton.exoPlayer = null
                    }

                    exoPlayerManager("Normal")
                    uiControls()
                    viewModel.setStatitcs(it.name!!, it.id, it.type!!, getCountryCode(this@RadioPlayerActivity),
                        getDeviceId()
                    )
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }

        }

    }

    private fun getDeviceId(): String {
        val deviceID =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return deviceID
    }

    private fun downloadEpisode(data: Data) {
        AppSingelton.downloadingEpisodeData = data
//                    DownloadFile(data).execute()
//        if (AppSingelton.currentDownloading.matches("".toRegex())) {
        val data = data
        AppSingelton.downloadingEpisodeData = data
        downloadEpisodeNow(data, this)


//        }
    }

    private fun downloadEpisodeNow(data: Data, radioPlayerActivity: RadioPlayerActivity) {
        val fileName = data.id + "" + System.currentTimeMillis().toString() + ".mp3"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            values.put(
                MediaStore.Audio.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}"
            )
            val uri = contentResolver.insert(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values
            )
            relativePath = getRealPathFromURI(this, uri!!)
        } else {
            Environment.getExternalStorageDirectory().path + "/${fileName}"
            val audio = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .toString(), fileName
            )

            relativePath = audio.path
        }
        AppSingelton.currentDownloading = data.id
        podEpisodesAdapter?.notifyDataSetChanged()
        downloadFile(fileName, "", data.audio, relativePath, data.title, data)
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, null)
        val idx: Int =
            if (contentUri.path!!.startsWith("/external/image") || contentUri.path!!.startsWith("/internal/image")) {
                cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            } else if (contentUri!!.path!!.startsWith("/external/video") || contentUri!!.path!!.startsWith(
                    "/internal/video"
                )
            ) {
                cursor!!.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
            } else if (contentUri.path!!.startsWith("/external/audio") || contentUri!!.path!!.startsWith(
                    "/internal/audio"
                )
            ) {
                cursor!!.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
            } else {
                return contentUri.path!!
            }
        return if (cursor != null && cursor.moveToFirst()) {
            cursor.getString(idx)
        } else ""
    }

    private fun downloadFile(
        fileName: String, desc: String, url: String, outputPath: String, title: String, data: Data
    ) {
        if (appDatabase == null) appDatabase = AppDatabase.getDatabaseClient(this)
        val file1 = File(relativePath)
        if (downloadManager != null && isDownloadingInProgress(this)) {
            showToast("Downloading in progress please wait a while...")
//            AppSingelton.currentDownloading = ""
            return
        } else {
            if (file1.exists()) {
                showToast("File Already Exist...")
                return
            }
            val request = DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setTitle("Downloading $title").setDescription(desc)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverRoaming(true).setAllowedOverMetered(true)
                .setDestinationUri(Uri.fromFile(file1))
//            .setDestinationInExternalPublicDir(relativePath, fileName)
            downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadId = downloadManager?.enqueue(request)!!
//        data.fileURI = Environment.DIRECTORY_DOWNLOADS+"/${fileName}"
            data.fileURI = relativePath
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase!!.appDap().insertOfflineEpisode(data)
            }
            data.videoID = downloadId.toInt()
            podEpisodesAdapter.notifyDataSetChanged()

        }


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isDownloadingInProgress(context: Context): Boolean {
//         downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING)

        val cursor = downloadManager?.query(query)
        val inProgress = cursor?.count!! > 0
        cursor?.close()

        return inProgress
    }

    private fun refreshAdapter() {
        podEpisodesAdapter = PodEpisodesAdapter(
            podcastEpisodeList!!, viewModel
        )
        dataBinding.podepisodeadapter = podEpisodesAdapter


    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        //below android 11
        ActivityCompat.requestPermissions(
            this, permissions, STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
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
            "Episodes",
            secondaryUrl = "",
            isBlocked = false,
            description = it.description
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
    }

    private fun _checkMediaType() {
        //Checking the Type of MEDIA
//        if (AppSingelton.exoPlayer != null)
        podcastType = AppSingelton.radioSelectedChannel.value?.type ?: ""
//        Toast.makeText(this@RadioPlayerActivity, podcastType, Toast.LENGTH_SHORT).show()
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

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        val item = mediaItem?.mediaMetadata
        val currentPos = mediaItem?.mediaId
        if (podcastType.matches("PODCAST".toRegex()) || podcastType.matches("Episodes".toRegex())) dataBinding.episode.text =
            podcastEpisodeList!![currentPos?.toInt() ?: 0].title

    }

    override fun onSetAlarm() {
        startActivity(Intent(this, AlarmFragment::class.java))
//        closePlayerandPanel()
    }

    override fun onShare() {
        AppConstants.share(
            "Checkout this link its amazing. ", AppSingelton._radioSelectedChannel.value, this
        )

    }

    override fun onSleepTimer() {
        startActivity(Intent(this, SleepTimerFragment::class.java))

    }

    override fun onFavourite() {
        AppSingelton._radioSelectedChannel.value?.let { it1 ->
            viewModel.addChannelToFavourites(
                it1
            )
        }
    }

    override fun onInternetAvailable() {
        if (!isInternetavailable) {
//            showToast("Internet connected")
            if (playwhenReady) dataBinding.playerView.player?.play()
            else {
                if (!isEpisode) exoPlayerManager("Normal")
                else exoPlayerManager("Episode")
            }
        }
        isInternetavailable = true
    }

    override fun onInternetUnavailable() {
        showToast("Internet connection is not available please check your internet connection")
        isInternetavailable = false
        dataBinding.progressDownload.visibility = View.VISIBLE
//        showToast("No internet available please check your internet connection")
        dataBinding.playerView.player?.pause()
    }

    fun getCountryCode(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            val countryCode = telephonyManager.networkCountryIso
            countryCode

        } catch (e: SecurityException) {
            e.printStackTrace()
            "Unknown"
        }
    }


    override fun onStart() {
        super.onStart()
        connectivityChecker.register()
        val filter = IntentFilter("DOWNLOAD_COMPLETE")
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, filter)

    }

    override fun onStop() {
        super.onStop()
        connectivityChecker.unregister()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadReceiver)

    }


    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == "DOWNLOAD_COMPLETE") {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                AppSingelton.currentDownloading = ""
                getofflinedata()
                podEpisodesAdapter.notifyDataSetChanged()

            }
        }
    }

    override fun onPause() {
        super.onPause()
        connectivityHandler.stopCheckingConnectivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityHandler.stopCheckingConnectivity()
    }

    override fun onDialogClose() {
        finish()
    }
}