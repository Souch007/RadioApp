package com.baidu.netcast.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.io.IOException
import java.util.Locale

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    fun getCurrentLocation(onSuccessListener: OnSuccessListener<Location>) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                .addOnSuccessListener { location: Location? ->
                    Log.d("getCurrentLocation1", "getCurrentLocation: ${location}")
                    Log.d(
                        "getCurrentLocation1",
                        "getCurrentLocation: ${location?.latitude.toString()}"
                    )
                    if (location == null) {
                        Toast.makeText(
                            context,
                            "Unable to get your location please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSuccessListener.onSuccess(null)
                    } else {
                        onSuccessListener.onSuccess(location)
                    }

                }


        }
    }


    fun getCountryFromLocation(location: LatLng): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses?.get(0)
                return address?.countryName
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}