package com.netcast.radio.request

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.netcast.radio.PlayingChannelData

object AppConstants {
    val ALARM_CHECKBOX: String = "alarm_checkbox"
    const val SELECTED_ALARM_RADIO: String = "alarm_radiodata"
    const val STOPFOREGROUND_ACTION: String = "STOP_SERVICE"
    const val NOTIFICATION_ID: Int = 5709
    const val RADIO_PLAYER_ACTIVITY: String = "RadioPlayerActivity"
    const val MAIN_ACTIVITY: String = "MainActivity"
    const val DownloadActivity: String = "DownloadActivity"
    const val BASE_URL = "https://apitest.netcast.com/"
    const val FETCH_RADIO = "getRadioListing"
    const val FETCH_MORERADIO = "categoryList"
    const val PODCAST_LISTING = "getPodcastListing"
    const val BLOCK_STATION = "blockChannel/"
    const val UN_BLOCK_STATION = "unblockChannel/"
    const val USER_STATS = "userstats"
    const val ALTERNATECHANNELS = "getAlternativeStations/"
    const val STATICS = "statistics"
    const val PODCAST_EPISODES = "getPodcastEpisodes/"
    const val GET_LANGUAGES = "getAllLanguages/"
    const val GET_ALL_COUNTRIES = "getAllCountries"
    const val GET_ALL_GENRES = "getAllGenres"
    const val SEARCH = "search/"
    const val GET_FREQUENT_SEARCH = "getFrequentSearchesTags/"
    const val PLAYER_SECS = "player_ffbbsec"
    const val SKIP_SLIENCE = "player_skip_silence"
    const val AUTO_PLAY_EPISODES = "auto_play_episodes"
    const val PREFIX = "https://netcast.page.link"
    const val UPDATE_REQUEST_CODE = 101
    fun share(messageToShare: String, appUrl: PlayingChannelData?, context: Context) {
        val appLinkUri = Uri.Builder()
            .scheme("https")
            .authority("netcast.com")
            .appendPath("channelsdata")
            .appendQueryParameter("channelName", appUrl?.name)
            .build()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageToShare)
            putExtra(Intent.EXTRA_TEXT, appLinkUri.toString())
        }

        context.startActivity(Intent.createChooser(shareIntent, messageToShare))
    }

    fun shareAppLinkWithFallback(context: Context, message: String) {
        // Base App Link
        val appLinkUri = "intent://channelsdata/#Intent;scheme=https;" +
                "package=com.netcast.radio;" +
                "S.browser_fallback_url=https://play.google.com/store/apps/details?id=com.netcast.radio;end"

        // Combine the message with the App Link
        val shareContent = "$message\n$appLinkUri"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareContent)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share link via"))
    }
}