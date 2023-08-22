package com.netcast.radio.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.netcast.radio.MainActivity
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton

class SplashActivity : AppCompatActivity() {
    private  val TAG = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },2000)
        handleIncomingDeepLinks()


       /* Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }*/
    }

    private fun handleIncomingDeepLinks() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                deepLink?.let { uri ->
                    val postId = uri.toString().substring(deepLink.toString().lastIndexOf("/") + 1)
                    when {
                        uri.toString().contains("channels") -> {
//                            navigateToNewsFeed(Gs)
                            AppSingelton._radioSelectedChannel.value=Gson().fromJson(postId,PlayingChannelData::class.java)

                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "handleIncomingDeepLinks: ${it.message}")
            }
    }
}