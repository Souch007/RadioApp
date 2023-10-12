package com.netcast.radio.request

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.gson.Gson
import com.netcast.radio.PlayingChannelData

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

   fun share(messageToShare: String, appUrl: PlayingChannelData?,context: Context) {
      AppConstants.generateSharingLink(
//            deepLink = AppConstants.PREFIX.toUri(),
         deepLink = Uri.parse("https://netcast.com/"),
         Gson().toJson(appUrl)
      ) { generatedLink ->
         shareDeepLink(messageToShare,generatedLink,context)
      }

   }

   private fun shareDeepLink(message :String, deepLink: String,context:Context) {
      val intent = Intent(Intent.ACTION_SEND)
      intent.type = "text/plain"

      intent.putExtra(Intent.EXTRA_TEXT, message + "\n" + deepLink)

      intent.putExtra(Intent.EXTRA_TEXT, deepLink)
      context.startActivity(intent)


   }


   fun generateSharingLink(
      deepLink: Uri,
      channeldata:String,
      getShareableLink: (String) -> Unit = {},
   ) {
      FirebaseDynamicLinks.getInstance().createDynamicLink().run {
         // What is this link parameter? You will get to know when we will actually use this function.
         link = deepLink
         domainUriPrefix = PREFIX
         link = link!!.buildUpon().appendQueryParameter("channeldata", channeldata).build()

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