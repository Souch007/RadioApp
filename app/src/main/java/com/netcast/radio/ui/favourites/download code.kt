package com.netcast.radio.ui.favourites

class `download code` {


    import android.app.DownloadManager
    import android.content.Context
    import android.net.Uri
    import android.os.Environment
    import android.widget.Toast

// ...

    inner class DownloadTask(private val context: Context, private val item: DownloadItem, private val progressBar: ProgressBar) :
        AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val request = DownloadManager.Request(Uri.parse("YOUR_FILE_URL")) // Replace with your file URL
                .setTitle("File Download") // Set the title of the download notification
                .setDescription("Downloading") // Set the description of the download notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true) // Set if download can proceed over a metered network
                .setAllowedOverRoaming(true) // Set if download can proceed over a roaming network

            // Set the destination in the external storage directory
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "YourFile" // Replace with the desired file name
            )

            // Enqueue the download and get the download ID
            val downloadId = downloadManager.enqueue(request)

            // Monitor the download progress
            val query = DownloadManager.Query().setFilterById(downloadId)
            var downloading = true

            while (downloading) {
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val bytesDownloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val progress = ((bytesDownloaded.toDouble() / bytesTotal.toDouble()) * 100).toInt()

                    // Update progress
                    publishProgress(progress)

                    // Check if download is complete
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }
                }
                cursor.close()
                Thread.sleep(500) // Sleep for a while before checking the progress again
            }

            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            values[0]?.let {
                // Update progress in UI
                progressBar.progress = it
                item.progress = it
            }
        }

        override fun onPostExecute(result: Void?) {
            // Download completed
            notifyDataSetChanged()
            Toast.makeText(
                context,
                "Download completed for ${item.itemName}",
                Toast.LENGTH_SHORT
            ).show()
            progressBar.visibility = View.GONE
        }
    }


    ------------------------------------------------------------------------------------------------
    private fun startDownload(item: DownloadItem, progressBar: ProgressBar) {
        // TODO: Implement your download logic here
        // You should use AsyncTask, Coroutine, or any other method to perform the download in the background
        // Update the progress by calling progressBar.setProgress(progress)
        // When the download is complete, hide the progress bar
        // For simplicity, I'm using a Handler to simulate the progress
        simulateDownloadProgress(item, progressBar)
    }

    private fun simulateDownloadProgress(item: DownloadItem, progressBar: ProgressBar) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentProgress = progressBar.progress
                if (currentProgress < 100) {
                    progressBar.progress += 10
                    handler.postDelayed(this, 500)
                } else {
                    // Download completed
                    item.progress = 100
                    notifyDataSetChanged()
                    Toast.makeText(
                        progressBar.context,
                        "Download completed for ${item.itemName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }
        }
        progressBar.visibility = View.VISIBLE
        handler.post(runnable)
    }



---------------------------------------------------------------------------
    class DownloadAdapter(private val items: List<DownloadItem>) :
        RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

        // ...

        inner class DownloadTask(private val item: DownloadItem, private val progressBar: ProgressBar) :
            AsyncTask<Void, Int, Void>() {

            override fun doInBackground(vararg params: Void?): Void? {
                // TODO: Implement your actual download logic here
                // You should update progress by calling publishProgress(progress)
                // Return the result when the download is complete
                for (progress in 0..100 step 10) {
                    publishProgress(progress)
                    Thread.sleep(500)
                }
                return null
            }

            override fun onProgressUpdate(vararg values: Int?) {
                values[0]?.let {
                    // Update progress in UI
                    progressBar.progress = it
                    item.progress = it
                }
            }

            override fun onPostExecute(result: Void?) {
                // Download completed
                notifyDataSetChanged()
                Toast.makeText(
                    progressBar.context,
                    "Download completed for ${item.itemName}",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            holder.btnDownload.setOnClickListener {
                val downloadTask = DownloadTask(item, holder.progressBar)
                downloadTask.execute()
            }

            holder.progressBar.progress = item.progress
        }

        // ...
    }
    ----------------------------------


    override fun doInBackground(vararg params: Void?): Void? {
        // TODO: Implement your actual download logic here
        // You should update progress by calling publishProgress(progress)
        // Return the result when the download is complete

        // Example: Downloading a file and updating progress
        val fileSize = 1000 // Replace with the actual file size
        var downloadedSize = 0

        while (downloadedSize < fileSize) {
            // Simulate downloading a chunk of data
            downloadedSize += 50
            val progress = (downloadedSize.toDouble() / fileSize.toDouble() * 100).toInt()
            publishProgress(progress)
            Thread.sleep(100) // Simulate delay for demonstration purposes
        }

        return null
    }
}