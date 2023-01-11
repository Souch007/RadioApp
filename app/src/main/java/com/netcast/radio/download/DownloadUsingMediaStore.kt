package com.netcast.radio.download

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import java.io.*
import java.net.URL

class DownloadUsingMediaStore (var data: Data, var context: Context) :
    AsyncTask<String?, Int?, String?>() {
        var relativePath = ""
        protected override fun doInBackground(vararg p0: String?): String? {
            var count: Int
            val audioOutStream: OutputStream
            try {
                val fileName = data._podid.toString() + "" + System.currentTimeMillis().toString() + ".mp3"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues()
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                    values.put(
                        MediaStore.Audio.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_MUSIC}"
                    )
                    val uri = context.contentResolver.insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                    audioOutStream = context.contentResolver.openOutputStream(uri!!)!!
                    relativePath= getRealPathFromURI(context,uri)
                } else {
                    Environment.getExternalStorageDirectory().path + "/${fileName}"
                    val audio = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString(), fileName)
                    audioOutStream = FileOutputStream(audio)
                    relativePath =audio.path
                }
                AppSingelton.currentDownloading = data._podid.toString()
                val _url = URL(data.enclosureUrl)
                val conexion = _url.openConnection()
                conexion.connect()
                val lenghtOfFile = conexion.contentLength
                val input: InputStream = BufferedInputStream(_url.openStream())

                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    publishProgress((total * 100 / lenghtOfFile).toInt())
                    audioOutStream.write(data, 0, count)
                }
                audioOutStream.flush()
                audioOutStream.close()
                input.close()
            }
            catch (e: Exception) {
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
                    if (current == 100) {
                        data.fileURI = relativePath
                        if (AppSingelton.downloadedIds.matches("".toRegex())) {
                            AppSingelton.downloadedIds = data._podid.toString()
                        } else if (!AppSingelton.downloadedIds.contains(data._podid.toString() + ""))
                            AppSingelton.downloadedIds =
                                AppSingelton.downloadedIds + "," + data._podid.toString()
                        AppSingelton.currentDownloading = ""
                        AppSingelton._onDownloadCompletion.value = data
                    }
                }
            }
        }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, null)
        val idx: Int = if (contentUri.path!!.startsWith("/external/image") || contentUri.path
                !!.startsWith("/internal/image")
        ) {
            cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        } else if (contentUri!!.path!!.startsWith("/external/video") || contentUri!!.path
                !!.startsWith("/internal/video")
        ) {
            cursor!!.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
        } else if (contentUri.path!!.startsWith("/external/audio") || contentUri!!.path
                !!.startsWith("/internal/audio")
        ) {
            cursor!!.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
        } else {
            return contentUri.path!!
        }
        return if (cursor != null && cursor.moveToFirst()) {
            cursor.getString(idx)
        } else ""
    }

    }