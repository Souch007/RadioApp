package com.netcast.radio.base

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
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

    override fun onCreate() {
        super.onCreate()
        initListener(AppSingelton._currentPlayingChannel.value)

    }

    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action.equals(AppConstants.STOPFOREGROUND_ACTION)) {
            stopservice()
            stopSelfResult(startId)
        }
        return START_NOT_STICKY
    }*/

    private fun initListener(_currentPlayingChannel: PlayingChannelData?) {

        val notificationId = AppConstants.NOTIFICATION_ID
        val mediaDescriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentSubText(player: Player): CharSequence? {
                return "netcast"
            }

            override fun getCurrentContentTitle(player: Player): String {
                return _currentPlayingChannel!!.name ?: "No Name"
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): String {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(_currentPlayingChannel!!.country, Html.FROM_HTML_MODE_COMPACT)
                        .toString()
                } else {
                    Html.fromHtml(_currentPlayingChannel!!.country).toString()
                }
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                var trackImage: Bitmap? = null
                val thread = Thread {
                    try {
                        val uri = Uri.parse(_currentPlayingChannel!!.favicon)
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
                    if (ongoing)
                        startForeground(notificationId, notification)
                    else {
                        stopservice()
                    }
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
//                    releasePlayer()
                }

            })
            .build()

        playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_LOW)
        playerNotificationManager!!.setUsePlayPauseActions(true)
        playerNotificationManager!!.setUseRewindActionInCompactView(true)
        playerNotificationManager!!.setUseFastForwardActionInCompactView(true)
        playerNotificationManager!!.setSmallIcon(R.drawable.logo)
        playerNotificationManager!!.setColorized(true)
        playerNotificationManager!!.setColor(0xFFBDBDBD.toInt())
        playerNotificationManager!!.setPlayer(AppSingelton.exoPlayer)
    }

    private fun stopservice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
        } else {
            stopForeground(true)
        }
//        playerNotificationManager?.setPlayer(null)
//        releasePlayer()
    }


    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        AppSingelton.exoPlayer?.release()
        AppSingelton.exoPlayer = null
//        AppSingelton._currentPlayingChannel.value=null

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (AppSingelton.exoPlayer?.isPlaying == false)
           stopservice()

    }
}