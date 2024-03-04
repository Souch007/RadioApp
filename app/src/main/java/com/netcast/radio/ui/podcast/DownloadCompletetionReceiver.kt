package com.netcast.radio.ui.podcast

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.netcast.radio.base.AppSingelton

class DownloadCompletetionReceiver :BroadcastReceiver() {
    override fun onReceive(p0: Context?, action: Intent?) {
//        if (action != null && action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            AppSingelton.currentDownloading=""
            LocalBroadcastManager.getInstance(p0!!).sendBroadcast(Intent("DOWNLOAD_COMPLETE"))

        }

//    }
}