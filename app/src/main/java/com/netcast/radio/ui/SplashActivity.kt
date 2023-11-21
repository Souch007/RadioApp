package com.netcast.radio.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.netcast.radio.MainActivity
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.util.LocationHelper
import com.netcast.radio.util.LocationUtils

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        locationandShareSettings()
        val appmode = getSharedPreferences("appData", Context.MODE_PRIVATE).getInt("App_Mode", -1)

        if (appmode == 0)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    private fun locationandShareSettings() {
        if (!LocationUtils.isGpsEnabled(this)) {
            LocationUtils.showEnableLocationDialog(this, onNegativeButtonClicked = {
                navigateToMain()
            })
        } else {
            if (checkLocationPermission()) {
                // Permissions are granted, create LocationHandler
                getCurrentLocationAndCountry()
            } else {
//                navigateToMain()
            }

        }

        handleIncomingDeepLinks()
    }

    private fun handleIncomingDeepLinks() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                deepLink?.let { uri ->
                    val channeldataJson = deepLink.getQueryParameter("channeldata")

//                    val postId = uri.toString().substring(deepLink.toString().lastIndexOf("/") + 1)
                    when {
                        uri.toString().contains("channels") -> {
//                            navigateToNewsFeed(Gs)
                            AppSingelton._radioSelectedChannel.value =
                                Gson().fromJson(channeldataJson, PlayingChannelData::class.java)

                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "handleIncomingDeepLinks: ${it.message}")
            }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndCountry()
            } else {
//                navigateToMain()
            }
        }
    }

    private fun getCurrentLocationAndCountry() {
        val locationHelper = LocationHelper(this)
        locationHelper.getCurrentLocation { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val country = locationHelper.getCountryFromLocation(currentLatLng)
                if (country != null) {
                    println("Current Country: $country")
                    AppSingelton.country = country
                    navigateToMain()
                } else {
                    navigateToMain()
                }
            }
        }

    }

    private fun navigateToMain() {
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationandShareSettings()
        /*if (requestCode == 10010 && resultCode == RESULT_OK) {

        } else {
            navigateToMain()
        }*/
    }
}