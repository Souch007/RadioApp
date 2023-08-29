package com.netcast.radio.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.Locale

class LocationUtils {

    companion object {
        fun isGpsEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun showEnableLocationDialog(
            context: Context,
            onNegativeButtonClicked: (() -> Unit)? = null
        ) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.apply {
                setTitle("Enable GPS")
                setMessage("GPS is required for this app. Please enable GPS in settings.")
                setPositiveButton("Settings") { _, _ ->

                    val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    (context as AppCompatActivity).startActivityForResult(
                        locationSettingsIntent,
                        10010
                    )
                }
                setNegativeButton("Cancel") { _, listner ->

                    onNegativeButtonClicked?.invoke()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }
}