package com.netcast.radio.base

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.request.AppConstants
import java.util.concurrent.ExecutionException

class AudioPlayerService : LifecycleService() {
    var playerNotificationManager: PlayerNotificationManager? = null
    private var audioManager: AudioManager? = null
    private var audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener? = null

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, AudioPlayerService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, AudioPlayerService::class.java)
            context.stopService(stopIntent)
        }

    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        initAudioFocusChangeListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
//        initListener(AppSingelton._currentPlayingChannel.value)
        if (requestAudioFocus()) {
            initListener(AppSingelton._currentPlayingChannel.value)
        } else {
            // Handle the case where audio focus is not granted
            stopForeground(true);
            stopSelf()
        }
        return START_NOT_STICKY
    }
    private fun requestAudioFocus(): Boolean {
        val result = audioManager?.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun releaseAudioFocus() {
        audioManager?.abandonAudioFocus(audioFocusChangeListener)
    }

    private fun initAudioFocusChangeListener() {
        audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    AppSingelton.exoPlayer?.playWhenReady = false
                }

                AudioManager.AUDIOFOCUS_GAIN -> {
                    AppSingelton.exoPlayer?.playWhenReady = true
                }
            }
        }
    }

    private fun initListener(_currentPlayingChannel: PlayingChannelData?) {

        val notificationId = AppConstants.NOTIFICATION_ID
        val mediaDescriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentSubText(player: Player): CharSequence? {
                return "netcast"
            }

            override fun getCurrentContentTitle(player: Player): String {
//                return _currentPlayingChannel!!.name ?: "No Name"
                val currentMediaIndex = player.currentWindowIndex
                val currentMediaItem = AppSingelton.mediaItemList?.get(currentMediaIndex)
                return currentMediaItem?.mediaMetadata?.title.toString()

            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): String {
                val currentMediaIndex = player.currentWindowIndex
                val currentMediaItem = AppSingelton.mediaItemList?.get(currentMediaIndex)

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(
                        currentMediaItem?.mediaMetadata?.description.toString(),
                        Html.FROM_HTML_MODE_COMPACT
                    )
                        .toString()
                } else {
                    Html.fromHtml(currentMediaItem?.mediaMetadata?.description.toString())
                        .toString()
                }
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                val currentMediaIndex = player.currentWindowIndex
                val currentMediaItem = AppSingelton.mediaItemList?.get(currentMediaIndex)

                var trackImage: Bitmap? = null
                val thread = Thread {
                    try {
                        val uri = currentMediaItem?.mediaMetadata?.artworkUri
                        val bitmap = Glide.with(applicationContext)
                            .asBitmap()
                            .load(uri)
                            .submit().get()
                        trackImage = bitmap
                        callback.onBitmap(bitmap)
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                thread.start()
                return trackImage
            }
        }

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            notificationId, "My_channel_id"
        )
            .setChannelNameResourceId(R.string.app_name)
            .setChannelDescriptionResourceId(R.string.notification_Channel_Description)
            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            startForeground(notificationId, notification)
                        } else {
                            startForeground(
                                notificationId, notification,
                               FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                            )
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(notificationId)
                        } else {
                            stopForeground(true)
                        }
//                       Companion.stopService(this@AudioPlayerService)
                    }
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    /*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    stopForeground(notificationId)
                                } else {
                                    stopForeground(true)
                                }
                                stopSelf()*/
//                    releasePlayer()

                }

            })
            .build()

        playerNotificationManager!!.setUsePlayPauseActions(true)
        playerNotificationManager!!.setUseStopAction(false)
        playerNotificationManager!!.setUseNextAction(true)
        playerNotificationManager!!.setUseNextActionInCompactView(true)
        playerNotificationManager!!.setUsePreviousActionInCompactView(true)
        playerNotificationManager!!.setUsePreviousAction(true)
        playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_LOW)
        playerNotificationManager!!.setUsePlayPauseActions(true)
        playerNotificationManager!!.setUseRewindActionInCompactView(true)
        playerNotificationManager!!.setUseFastForwardActionInCompactView(true)
        playerNotificationManager!!.setUsePreviousAction(true)
        playerNotificationManager!!.setUseNextAction(true)
        playerNotificationManager!!.setSmallIcon(R.drawable.logo)
        playerNotificationManager!!.setColorized(true)
        playerNotificationManager!!.setColor(0xFFBDBDBD.toInt())
        playerNotificationManager!!.setPlayer(AppSingelton.exoPlayer)

    }

    override fun onDestroy() {
        Log.d("onDestroyAudio","onDestroy: ")
        releasePlayer()
        releaseAudioFocus()
        stopForeground(true);
        stopSelf();
        super.onDestroy()
    }

    private fun releasePlayer() {
        AppSingelton.exoPlayer?.stop()
        AppSingelton.exoPlayer?.release()
        AppSingelton.exoPlayer = null
        AppSingelton._radioSelectedChannel.value = null

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
//        if (AppSingelton.exoPlayer?.isPlaying == false)
        Companion.stopService(this)

    }

}