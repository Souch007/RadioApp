package com.netcast.radio.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.widget.Toast

class ConnectivityHandler(private val context: Context) {
    private val handler = Handler()
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val runnable = object : Runnable {
        override fun run() {
            // Check for internet connectivity
            val isConnected = isNetworkAvailable()

            if (!isConnected) {
                showToast("Internet connection is not available please check your internet connection")
            }

            // Post the same runnable again after 10 seconds
            handler.postDelayed(this, 10000)
        }
    }

    fun startCheckingConnectivity() {
        // Start the recurring task
        handler.postDelayed(runnable, 10000)
    }

    fun stopCheckingConnectivity() {
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }

    private fun isNetworkAvailable(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
