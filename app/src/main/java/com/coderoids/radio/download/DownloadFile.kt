package com.coderoids.radio.download

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.coderoids.radio.base.AppSingelton
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class DownloadFile(private val podcastURL: String) :
    AsyncTask<String?, Int?, String?>() {
    protected override fun doInBackground(vararg p0: String?): String? {
        var count: Int
        try {
            val _url = URL(podcastURL)
            val conexion = _url.openConnection()
            conexion.connect()
            val lenghtOfFile = conexion.contentLength
            val input: InputStream = BufferedInputStream(_url.openStream())
            val relativePath =
                Environment.getExternalStorageDirectory().path + "/DownloadPodcastM.mp3"
            val output: OutputStream = FileOutputStream(relativePath)
            val data = ByteArray(1024)
            var total: Long = 0
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                // publishing the progress....
                publishProgress((total * 100 / lenghtOfFile).toInt())
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val current = values[0]
        if (current != null) {
            if (current % 5 == 0) {
                Log.d("Downloading", current.toString() + "")
                AppSingelton._progressPublish.value = current
            }
        }
    }
}