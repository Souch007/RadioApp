package com.netcast.radio.download

import android.os.AsyncTask
import android.os.Environment
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class DownloadFile(var data: Data) :
    AsyncTask<String?, Int?, String?>() {
    var relativePath = ""
    protected override fun doInBackground(vararg p0: String?): String? {
        var count: Int
        try {
            AppSingelton.currentDownloading = data._id.toString()
            val _url = URL(data.enclosureUrl)
            val conexion = _url.openConnection()
            conexion.connect()
            val lenghtOfFile = conexion.contentLength
            val input: InputStream = BufferedInputStream(_url.openStream())
            val fileName  = data._id.toString()+""+System.currentTimeMillis().toString()+".mp3"
            relativePath =
                Environment.getExternalStorageDirectory().path + "/${fileName}"
            val output: OutputStream = FileOutputStream(relativePath)
            val data = ByteArray(1024)
            var total: Long = 0
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                publishProgress((total * 100 / lenghtOfFile).toInt())
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
            AppSingelton.currentDownloading = ""
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val current = values[0]
        if (current != null && current >= 0) {
            if (current % 5 == 0) {
                AppSingelton._progressPublish.value = current
                if(current == 100){
                    data.fileURI = relativePath;
                    if(AppSingelton.downloadedIds.matches("".toRegex())){
                        AppSingelton.downloadedIds = data._id.toString()
                    } else if(!AppSingelton.downloadedIds.contains(data._id.toString()+""))
                        AppSingelton.downloadedIds = AppSingelton.downloadedIds + ","+ data._id.toString()
                    AppSingelton.currentDownloading = ""
                    AppSingelton._onDownloadCompletion.value = data
                }
            }
        }
    }
}