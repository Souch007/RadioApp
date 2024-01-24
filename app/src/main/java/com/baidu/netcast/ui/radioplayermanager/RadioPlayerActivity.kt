package com.baidu.netcast.ui.radioplayermanager

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.media.session.PlaybackStateCompat
import android.text.Html
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
import com.baidu.netcast.BR
import com.baidu.netcast.PlayingChannelData
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseActivity
import com.baidu.netcast.databinding.ActivityRadioPlayerBinding
import com.baidu.netcast.db.AppDatabase
import com.baidu.netcast.request.AppConstants
import com.baidu.netcast.request.Resource
import com.baidu.netcast.ui.radio.data.temp.RadioLists
import com.baidu.netcast.ui.radioplayermanager.adapter.PodEpisodesAdapter
import com.baidu.netcast.ui.radioplayermanager.episodedata.Data
import com.baidu.netcast.ui.ui.settings.AlarmFragment
import com.baidu.netcast.ui.ui.settings.SleepTimerFragment
import com.baidu.netcast.util.BottomSheetOptionsFragment
import com.baidu.netcast.util.OptionsClickListner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
class RadioPlayerActivity() : BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>(),
    OptionsClickListner {
    var podcastEpisodeList: List<Data>? = null
    var podcastType: String = ""
    lateinit var radioPlayerAVM: RadioPlayerAVM
    private var STORAGE_PERMISSION_REQUEST_CODE: Int = 5049
    private var isActivityLoaded = false
    var relativePath = ""
    private var downloadManager: DownloadManager? = null
    lateinit var podEpisodesAdapter: PodEpisodesAdapter
    var isEpisode=false

    companion object {
        private const val CHANNEL_ID = "download_channel"
        private const val FILE_URL = "https://www.example.com/file.pdf"
        private const val FILE_NAME = "file.pdf"
        private const val DOWNLOAD_NOTIFICATION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkWifiPlaySettings()) {
            createActivity()
            isActivityLoaded = true
        } else {
            Toast.makeText(
                this, "Please check your wifi as you enabled stream over wifi", Toast.LENGTH_LONG
            ).show()
            finish()
        }


    }

    private val permissions = arrayOf(
        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, READ_MEDIA_AUDIO
    )


    private fun createActivity() {
        AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
        if (AppSingelton.suggestedRadioList != null) viewModel.suggestedRadioList = AppSingelton.suggestedRadioList!!
        radioPlayerAVM = viewModel
        checkWifiPlaySettings()

        AppSingelton.radioSelectedChannel.observe(this) {
            it?.let {
                dataBinding.tvChannelName.text = it.name
            }

        }
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

        dataBinding.icPlay.setOnClickListener {
            if (dataBinding.playerView.player?.isPlaying == true) {
                dataBinding.playerView.player?.pause()
                dataBinding.icPlay.setImageResource(com.baidu.netcast.R.drawable.play_button)
            } else {
                dataBinding.playerView.player?.play()
                dataBinding.icPlay.setImageResource(com.baidu.netcast.R.drawable.pause_button)
            }

        }
        dataBinding.playerOptions.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetFragment = BottomSheetOptionsFragment(this,true,isEpisode)
        bottomSheetFragment.show(supportFragmentManager, "BSDialogFragment")

    }

    private fun checkWifiPlaySettings(): Boolean {
        val iswifiplay = sharedPreferences.getBoolean("stream_over_wifi", false)
        if (iswifiplay && !isWifiConnected(this)) {
            return true
        }
        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
//        if (checkWifiPlaySettings()) {
        if (AppSingelton._radioSelectedChannel?.value?.type == "Offline" && !isActivityLoaded) {
            createActivity()
        } else {
            AppSingelton.currentActivity = AppConstants.RADIO_PLAYER_ACTIVITY
            if (dataBinding.podepisodeadapter != null) {
                dataBinding.podepisodeadapter!!.notifyDataSetChanged()
            }
        }/*} else {
            Toast.makeText(
                this,
                "Please check your wifi as you enabled stream over wifi",
                Toast.LENGTH_LONG
            ).show()

        }*/
    }


    private fun uiControls() {
        checkIfItemisInFav()
        dataBinding.ivBack.setOnClickListener {
            AppSingelton._isPlayerFragVisible.value = false
            finish()
        }
        Glide.with(dataBinding.ivChannelLogo.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.baidu.netcast.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(dataBinding.ivChannelLogo)

        Glide.with(dataBinding.backBlur.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.baidu.netcast.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(dataBinding.backBlur)
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
        dataBinding.playerView.player?.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    dataBinding.icPlay.setImageResource(com.baidu.netcast.R.drawable.pause_button)
                    dataBinding.progressDownload.visibility = View.INVISIBLE
                } else if (playbackState == PlaybackStateCompat.STATE_STOPPED) {
                    dataBinding.icPlay.setImageResource(com.baidu.netcast.R.drawable.play_button)

                }
            }

            fun onPlayWhenReadyCommitted() {}
            fun onPlayerError(error: ExoPlaybackException?) {
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
        val isAutoPlayEnable = sharedPreferences.getBoolean(AppConstants.AUTO_PLAY_EPISODES, false)
        AppSingelton._radioSelectedChannelId = AppSingelton.radioSelectedChannel.value?.id ?: ""
        val currentPlayingUUid = AppSingelton._currenPlayingChannelId
        if (AppSingelton.exoPlayer == null || !AppSingelton._radioSelectedChannelId.matches(currentPlayingUUid.toRegex())) {
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
                )


                    .build().also { exoPlayer ->
                        var file = File(AppSingelton._radioSelectedChannel.value!!.url)
                        if (file.exists()) {
                            dataBinding.playerView.player = exoPlayer


                            val filePath = AppSingelton._radioSelectedChannel.value!!.url
                            val uri: Uri = Uri.parse(filePath)

                            // Prepare media item and start playback
                            val mediaItem = MediaItem.fromUri(uri)
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.play()
                            exoPlayer.addListener(this)
//                            val mediaItem = MediaItem.fromUri(file.toUri())
//                            exoPlayer.setMediaItem(mediaItem)

                        }
                    }
                //Log("Offline", "Request Recieved")
            }
            else {
                val allocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)
                val loadControl = DefaultLoadControl.Builder().setAllocator(allocator)
                    .setTargetBufferBytes(C.LENGTH_UNSET)
                    .setBufferDurationsMs(10000, 120000, 1000, 1000)
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
                        )/*.setPauseAtEndOfMediaItems(
                            sharedPreferences.getBoolean(
                                AppConstants.SKIP_SLIENCE, false
                            )
                        )*/.setHandleAudioBecomingNoisy(true).build().also { exoPlayer ->
                            val currentChannel = AppSingelton.radioSelectedChannel.value
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

                                AppSingelton.mediaItemList=mediaitems
                                exoPlayer.setMediaItems(mediaitems)
                            } else {
                                val mediaitemschannels = mutableListOf<MediaItem>()
                                val list=AppSingelton.suggestedRadioList?.toMutableList()
                                val selectedRadio=RadioLists(currentChannel?.country ?: "",currentChannel?.favicon ?: "",currentChannel?.id ?: "",currentChannel?.name ?: "",currentChannel?.url ?: "")
//                                AppSingelton.selectedChannel?.let { list?.add(0, it) }
                                list?.add(0,selectedRadio)
                                AppSingelton.suggestedRadioList=list
                                for (i in 0 until AppSingelton.suggestedRadioList!!.size) {
                                    val mediaMetadata = MediaMetadata.Builder()
                                        .setTitle(AppSingelton.suggestedRadioList!![i].name)
                                        .setDescription(AppSingelton.suggestedRadioList!![i].country)
                                        .setArtworkUri(Uri.parse(AppSingelton.suggestedRadioList!![i].favicon))
                                        .build()


                                    val mediaItem: MediaItem = MediaItem.Builder()
                                        .setUri(AppSingelton.suggestedRadioList!![i].url.toUri())
                                        .setMediaMetadata(mediaMetadata)

                                        .setMediaId(i.toString()).setTag(i).build()
                                    mediaitemschannels.add(mediaItem)
//                                mediaitems.add(MediaItem.fromUri(podcastEpisodeList!![i].audio))
                                }
                                AppSingelton.mediaItemList=mediaitemschannels
                                exoPlayer.setMediaItems(mediaitemschannels)
//                                val mediaItem = MediaItem.fromUri(url ?: "")
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
                isEpisode=true
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
//                    podEpisodesAdapter.downloadEpisode(it)
                    } else {
                        Toast.makeText(
                            this,
                            "Your wifi is not enable if you want to download episode over data please disable download over wifi option from settings",
                            Toast.LENGTH_LONG
                        ).show()
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

        viewModel._radioClicked.observe(this@RadioPlayerActivity) {
            try {
                exoPlayerManager("Normal")
                uiControls()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /* viewModel._radioClicked.observe(this@RadioPlayerActivity) {
             try {
                 exoPlayerManager("Normal")
                 uiControls()
             } catch (ex: Exception) {
                 ex.printStackTrace()
             }
         }*/
    }
    private fun downloadEpisode(data: Data) {
        AppSingelton.downloadingEpisodeData = data
//                    DownloadFile(data).execute()
        if (AppSingelton.currentDownloading.matches("".toRegex())) {
            val data = data
            AppSingelton.downloadingEpisodeData = data
            downloadEpisodeNow(data, this)
//            DownloadUsingMediaStore(data, this).execute()
            /*       startActivity(
                       Intent(
                           this@RadioPlayerActivity, DownloadActivity::class.java
                       )
                   )*/

        }
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
            Toast.makeText(
                this, "Downloading in progress please wait a while...", Toast.LENGTH_SHORT
            ).show()
//            AppSingelton.currentDownloading = ""
            return
        } else {
            if (file1.exists()) {
                Toast.makeText(this, "File Already Exist...", Toast.LENGTH_SHORT).show()
//                showAlertDialog("Alert", "File Already Exist")
//                AppSingelton.currentDownloading = ""
                return
            }
            // fileName -> fileName with extension
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
            data.videoID= downloadId.toInt()
            podEpisodesAdapter.notifyDataSetChanged()
//            AppSingelton.currentDownloading = ""


        }


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
//        }
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
            "Episodes"
        )
        AppSingelton._radioSelectedChannel.value = playingChannelData
    }

    private fun _checkMediaType() {
        //Checking the Type of MEDIA
        if (AppSingelton.exoPlayer != null) podcastType = AppSingelton.radioSelectedChannel.value?.type ?: ""
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
            dataBinding.adapter = com.baidu.netcast.ui.radio.adapter.RadioFragmentAdapter(
                AppSingelton.suggestedRadioList ?: listOf(), viewModel, "public"
            )
        }
    }

    private fun checkPodCastEpisodes() {
        dataBinding.podcastLoader.visibility = View.VISIBLE
        viewModel.getPodcastEpisodes(AppSingelton.radioSelectedChannel.value!!.idPodcast ?: "")

    }

    override val layoutRes: Int
        get() = com.baidu.netcast.R.layout.activity_radio_player
    override val bindingVariable: Int
        get() = BR.radioplayervm
    override val viewModelClass: Class<RadioPlayerAVM>
        get() = RadioPlayerAVM::class.java

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        val item = mediaItem?.mediaMetadata
        val currentPos = mediaItem?.mediaId
        if (podcastType.matches("PODCAST".toRegex()) || podcastType.matches("Episodes".toRegex()))
        dataBinding.episode.text = podcastEpisodeList!![currentPos?.toInt() ?: 0].title

    }

    override fun onSetAlarm() {
        startActivity(Intent(this, AlarmFragment::class.java))
//        closePlayerandPanel()
    }

    override fun onShare() {
        AppConstants.share(
            "Checkout this link its amazing. ",
            AppSingelton._radioSelectedChannel.value,
            this
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
}