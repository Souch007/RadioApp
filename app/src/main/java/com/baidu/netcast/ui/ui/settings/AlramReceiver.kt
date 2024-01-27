package com.baidu.netcast.ui.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import com.baidu.netcast.PlayingChannelData
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.AudioPlayerService
import com.baidu.netcast.request.AppConstants
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.gson.Gson


class AlramReceiver : BroadcastReceiver(), Player.Listener {
    lateinit var sharedPreferences: SharedPreferences
    private var context: Context? = null

    override fun onReceive(context: Context, p1: Intent?) {
        this.context = context
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder().build()
        notificationUtils.getManager().notify(150, notification)
        /*  val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("alaram_url", "text")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
*/

        sharedPreferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE)
        val renderersFactory = DefaultRenderersFactory(context)
        AppSingelton.exoPlayer?.let {
            it.release()
            it.stop()
        }
        val playingChannelData =
            retrieveStoredObject(AppConstants.SELECTED_ALARM_RADIO, PlayingChannelData::class.java)
//        val alarmCheckbox = sharedPreferences.getBoolean(AppConstants.ALARM_CHECKBOX, false)
        val alarmCheckbox = sharedPreferences.getBoolean("isAlarmSet", false)


        if (playingChannelData != null && alarmCheckbox) {
//        AppSingelton._radioSelectedChannel.value=playingChannelData
            AppSingelton._currentPlayingChannel.value = playingChannelData
            AppSingelton.exoPlayer =
                ExoPlayer.Builder(context, renderersFactory)
                    .setHandleAudioBecomingNoisy(true).build().also { exoPlayer ->
                        val mediaMetadata = MediaMetadata.Builder()
                            .setTitle(playingChannelData?.name)
                            .setDescription(playingChannelData?.country)
                            .setArtworkUri(Uri.parse(playingChannelData?.favicon)).build()

                        val mediaItem: MediaItem = MediaItem.Builder()
                            .setUri(playingChannelData?.url ?: "")
                            .setMediaMetadata(mediaMetadata)
                            .build()


//                        val mediaItem = MediaItem.fromUri(playingChannelData?.url ?: "")
//
//
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.addListener(this)
                        exoPlayer.prepare()
                        exoPlayer.play()

                        if (AppSingelton.mediaItemList.isNullOrEmpty()) {
                            val mediaitems = mutableListOf<MediaItem>()
                            mediaitems.add(mediaItem)
                            AppSingelton.mediaItemList = mediaitems
                        }
                        else{
                            AppSingelton.mediaItemList!!.add(0,mediaItem)
                        }
                    }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        val serviceIntent = Intent(context, AudioPlayerService::class.java)
        if (isPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioPlayerService.startService(context!!)
            } else {
                context?.startService(Intent(serviceIntent))
            }
        }
    }

    fun <T> retrieveStoredObject(prefName: String, baseClass: Class<T>): T? {
        val dataObject: String? = sharedPreferences.getString(prefName, "")
        return Gson().fromJson(dataObject, baseClass)
    }
}