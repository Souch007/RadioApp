package com.netcast.radio.download


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


class DownloadUsingMediaStore(var data: Data, var context: Context) :
    AsyncTask<String?, Int?, String?>() {
    var appDatabase: AppDatabase? = null
    private var notificationManager: NotificationManager? = null
    private val DOWNLOAD_NOTIFICATION_ID = 1
    var relativePath = ""
    var downloadId:Long = 0
    private var downloadManager:DownloadManager?=null

    private fun updateNotification(progress: Int) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, "download_channel")
                .setContentTitle("Downloading file")
                .setContentText("Please wait...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setProgress(100, progress, false)
                .setOnlyAlertOnce(true)
        val notification: Notification = builder.build()
        notificationManager!!.notify(DOWNLOAD_NOTIFICATION_ID, notification)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Channel"

            val description = "Channel for file downloads"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("download_channel", name, importance).apply {
                this.description = description
            }
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        } else {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }*/
    }

    override fun doInBackground(vararg p0: String?): String? {
        var count: Int
        val audioOutStream: OutputStream
        try {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            val fileName = data.id + "" + System.currentTimeMillis().toString() + ".mp3"
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
                relativePath = getRealPathFromURI(context, uri!!)
            } else {
                Environment.getExternalStorageDirectory().path + "/${fileName}"
                val audio = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString(), fileName)

                relativePath = audio.path
            }
            AppSingelton.currentDownloading = data.id.toString()

            downloadFile(fileName,"",data.audio,relativePath,data.title)
         /*   val _url = URL(data.audio)
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
            input.close()*/
        } catch (e: Exception) {
            e.printStackTrace()
            AppSingelton.currentDownloading = ""
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        values[0]?.let { updateNotification(it) }
        val current = values[0]
        if (current != null && current >= 0) {
            /*  CoroutineScope(Dispatchers.IO).launch {
                  updateProgress(current)
              }*/

            if (current % 5 == 0) {
                AppSingelton._progressPublish.value = current
                if (current == 100) {

                    data.fileURI = relativePath
                    if (AppSingelton.downloadedIds.matches("".toRegex())) {
                        AppSingelton.downloadedIds = data.id.toString()
                    } else if (!AppSingelton.downloadedIds.contains(data.id.toString() + ""))
                        AppSingelton.downloadedIds =
                            AppSingelton.downloadedIds + "," + data.id.toString()
                    AppSingelton.currentDownloading = ""
                    AppSingelton._onDownloadCompletion.value = data

                }
            }
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        notificationManager?.cancel(DOWNLOAD_NOTIFICATION_ID)
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

    private fun downloadFile(
        fileName: String,
        desc: String,
        url: String,
        outputPath: String,
        title: String
    ) {
        if (appDatabase == null)
            appDatabase = AppDatabase.getDatabaseClient(context)
        val file1 = File(relativePath)
        if (downloadManager!=null && isDownloadingInProgress(context)) {
            (context as Activity).runOnUiThread(Runnable {
                showAlertDialog("Alert","File Already Downlaoding")
            })
        } else {
            if (file1.exists()) {
                (context as Activity).runOnUiThread(Runnable {
                    showAlertDialog("Alert","File Already Exist")
                })

                file1.delete()
            }
            // fileName -> fileName with extension
            val request = DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setTitle("Downloading $title")
                .setDescription(desc)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)
                .setDestinationUri(Uri.fromFile(file1))
//            .setDestinationInExternalPublicDir(relativePath, fileName)
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            downloadId = downloadManager?.enqueue(request)!!
//        data.fileURI = Environment.DIRECTORY_DOWNLOADS+"/${fileName}"
            data.fileURI = relativePath
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase!!.appDap().insertOfflineEpisode(data)
            }
            AppSingelton.currentDownloading = ""
        }


    }

    private fun showAlertDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    private fun isDownloadingInProgress(context: Context): Boolean {
//         downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING)

        val cursor = downloadManager?.query(query)
        val inProgress = cursor?.count!! > 0
        cursor?.close()

        return inProgress
    }


}