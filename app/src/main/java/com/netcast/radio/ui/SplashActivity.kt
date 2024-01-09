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
import androidx.appcompat.app.AlertDialog
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
                getCurrentLocationAndCountry()
            } else {
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
                //Log(TAG, "handleIncomingDeepLinks: ${it.message}")
            }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            showCustomRationaleDialog()
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
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                navigateToMain()
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
    }
    private fun showCustomRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Location Permission Required")
            .setMessage("We need your location to provide you radio based on your Location")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                navigateToMain()
            }
            .create()
            .show()
    }

}