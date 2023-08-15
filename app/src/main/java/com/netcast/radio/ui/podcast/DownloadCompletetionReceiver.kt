package com.netcast.radio.ui.podcast

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.netcast.radio.base.AppSingelton

class DownloadCompletetionReceiver :BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        Toast.makeText(p0, "Completed", Toast.LENGTH_SHORT).show()
       AppSingelton.currentDownloading=""
    }
}