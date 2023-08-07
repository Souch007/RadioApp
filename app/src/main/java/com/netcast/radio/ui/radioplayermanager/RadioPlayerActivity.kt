package com.netcast.radio.ui.radioplayermanager

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.request.AppConstants
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


class RadioPlayerActivity() : BaseActivity<RadioPlayerAVM, ActivityRadioPlayerBinding>() {
    var podcastEpisodeList: List<Data>? = null
    var podcastType: String = ""
    lateinit var radioPlayerAVM: RadioPlayerAVM
    private var STORAGE_PERMISSION_REQUEST_CODE: Int = 5049
    private var isActivityLoaded = false
    var relativePath = ""
    private var downloadManager: DownloadManager? = null

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
        if (AppSingelton.suggestedRadioList != null) viewModel.suggestedRadioList =
            AppSingelton.suggestedRadioList!!
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
               dataBinding.icPlay.setImageResource(com.netcast.radio.R.drawable.play_button)
            }
            else{
                dataBinding.playerView.player?.play()
                dataBinding.icPlay.setImageResource(com.netcast.radio.R.drawable.pause_button)
            }

        }
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
            .error(com.netcast.radio.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(dataBinding.ivChannelLogo)

        Glide.with(dataBinding.backBlur.context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(com.netcast.radio.R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
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
                    dataBinding.icPlay.setImageResource(R.drawable.exo_icon_pause)
                } else if (playbackState == PlaybackStateCompat.STATE_STOPPED) {
                    dataBinding.icPlay.setImageResource(R.drawable.exo_icon_play)

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
                            val url = AppSingelton.radioSelectedChannel.value?.url
                            dataBinding.playerView.player = exoPlayer
                            if (isAutoPlayEnable && (podcastType.matches("PODCAST".toRegex()) || podcastType.matches(
                                    "Episodes".toRegex()
                                )) && !podcastEpisodeList.isNullOrEmpty()
                            ) {
                                val mediaitems = mutableListOf<MediaItem>()
                                for (i in 0 until podcastEpisodeList!!.size) {
                                    val mediaItem: MediaItem = MediaItem.Builder()
                                        .setUri(podcastEpisodeList!![i].audio.toUri())
                                        .setMediaId(i.toString()).setTag(i).build()
                                    mediaitems.add(mediaItem)
//                                mediaitems.add(MediaItem.fromUri(podcastEpisodeList!![i].audio))
                                }
                                exoPlayer.setMediaItems(mediaitems)
                            } else {
                                val mediaItem = MediaItem.fromUri(url ?: "")
                                exoPlayer.setMediaItem(mediaItem)
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
                    dataBinding.playerView.visibility = View.VISIBLE

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
        viewModel._onepisodeShareClicked.observe(this@RadioPlayerActivity) {
            try {
                share("Checkout this link its amazing. ", it.listennotesUrl)

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

    private fun share(messageToShare: String, appUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, messageToShare + "\n" + appUrl)
        startActivity(Intent(intent))
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
            AppSingelton.currentDownloading = ""
            return
        } else {
            if (file1.exists()) {
                Toast.makeText(this, "File Already Exist...", Toast.LENGTH_SHORT).show()
//                showAlertDialog("Alert", "File Already Exist")
                AppSingelton.currentDownloading = ""
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
            AppSingelton.currentDownloading = ""
        }


    }


    private fun showAlertDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message).setPositiveButton("OK", null).show()
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
        dataBinding.podepisodeadapter =
            com.netcast.radio.ui.radioplayermanager.adapter.PodEpisodesAdapter(
                podcastEpisodeList!!, viewModel
            )
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
        if (AppSingelton.exoPlayer != null) Log.d(
            "tag", AppSingelton.exoPlayer!!.audioSessionId.toString()
        )
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
                AppSingelton.suggestedRadioList ?: listOf(), viewModel
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

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        val item = mediaItem?.mediaMetadata
        val currentPos = mediaItem?.mediaId
        dataBinding.episode.text = podcastEpisodeList!![currentPos?.toInt() ?: 0].title

    }


}