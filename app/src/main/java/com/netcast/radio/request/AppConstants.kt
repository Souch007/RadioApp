package com.netcast.radio.request

import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters

object AppConstants {
 val ALARM_CHECKBOX: String="alarm_checkbox"
 const val SELECTED_ALARM_RADIO: String="alarm_radiodata"
    const val STOPFOREGROUND_ACTION: String = "STOP_SERVICE"
    const val NOTIFICATION_ID: Int = 5709
    const val RADIO_PLAYER_ACTIVITY: String = "RadioPlayerActivity"
    const val MAIN_ACTIVITY: String = "MainActivity"
    const val DownloadActivity: String = "DownloadActivity"
    const val BASE_URL = "https://apitest.netcast.com/"
    const val FETCH_RADIO = "getRadioListing"
    const val PODCAST_LISTING = "getPodcastListing"
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



   fun generateSharingLink(
      deepLink: Uri,

      getShareableLink: (String) -> Unit = {},
   ) {
      FirebaseDynamicLinks.getInstance().createDynamicLink().run {
         // What is this link parameter? You will get to know when we will actually use this function.
         link = deepLink
         domainUriPrefix = PREFIX
         // Pass your preview Image Link here;
       /*  setSocialMetaTagParameters(
            DynamicLink.SocialMetaTagParameters.Builder()
               .setImageUrl(previewImageLink)
               .build()
         )*/
         // Required
         androidParameters {
            build()
         }
         // Finally
         buildShortDynamicLink()
      }.also {
         it.addOnSuccessListener { dynamicLink ->
            getShareableLink.invoke(dynamicLink.shortLink.toString())
         }
         it.addOnFailureListener {ex->
            Log.d("TAG", "generateSharingLink: ${ex.message}")
         }
      }
   }
}