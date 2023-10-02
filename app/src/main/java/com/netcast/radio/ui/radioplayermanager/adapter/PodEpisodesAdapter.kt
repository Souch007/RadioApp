package com.netcast.radio.ui.radioplayermanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.downloader.OnDownloadListener
import com.downloader.OnProgressListener
import com.downloader.PRDownloader
import com.downloader.Progress
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.base.BaseViewHolder
import com.netcast.radio.databinding.PodcastEpisodesRowBinding

import com.netcast.radio.download.adapter.toPercent
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import java.io.File

val mutableMapOfIDToProgressListenerCallback: MutableMap<Int?, MyDownloadProgressListener> =
    mutableMapOf()

class PodEpisodesAdapter(
    private val list: List<Data>,
    private val _onEpisodeListener: OnEpisodeClickListener,
    private val context: Context
) : BaseAdapter<PodcastEpisodesRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.podcast_episodes_row
    private lateinit var databinding: PodcastEpisodesRowBinding
    private var currentItem: Data? = null
    private var adapterPos: Int = -1

    @SuppressLint("Range")
    override fun bind(binding: PodcastEpisodesRowBinding, item: Data, position: Int) {
        currentItem = item
        databinding = binding
        adapterPos = position
        binding.apply {
            episodeData = item
            listener = _onEpisodeListener

            executePendingBindings()
        }
        if (AppSingelton.downloadedIds.contains(item.id.toRegex())) {
//            binding.tvDownlaodTag.text = "Offline Available"
            binding.icDone.visibility = View.VISIBLE
            binding.icDownlaod.visibility = View.GONE
            binding.progressDownload.visibility = View.GONE
            binding.icDelete.visibility = View.VISIBLE
        } else {
            binding.icDelete.visibility = View.GONE
        }

        binding.icDelete.setOnClickListener {
            binding.icDelete.visibility = View.GONE
            binding.icDownlaod.visibility = View.VISIBLE
            binding.icDone.visibility = View.GONE
            _onEpisodeListener.onEpisodeDeleteClicked(item)
        }
        binding.icShare.setOnClickListener {
            _onEpisodeListener.onEpisodeShareClicked(item)
        }

        if (AppSingelton.currentDownloading!!.matches(item.id.toRegex())) {
            binding.progressDownload.visibility = View.VISIBLE
//            binding.tvDownlaodTag.text = "Downloading..."
//            binding.tvDownlaodTag.visibility = View.VISIBLE
            binding.icDone.visibility = View.GONE
            binding.icDownlaod.visibility = View.GONE
            binding.progressDownload.progress = currentItem?.percentDownloaded?.toInt()!!
        }


        /*if (item.videoID != 0L) {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(item.videoID)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {

                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val progress =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val total =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                if (status == DownloadManager.STATUS_RUNNING) {
                    Log.d("PROGRESSSBARRRR", "progress: $progress")
                    // Download is in progress
                    binding.progressDownload.visibility = View.VISIBLE
                    binding.progressDownload.max = total
                    binding.progressDownload.progress = progress

                } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // Download is complete
                    binding.progressDownload.visibility = View.GONE
                } else {
                    // Download failed or stopped
                    binding.progressDownload.visibility = View.GONE
                    item.videoID = -1L // Reset download ID
                }
            }
        } else {
            // Download not initiated
            binding.progressDownload.visibility = View.GONE
        }*/


    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<PodcastEpisodesRowBinding>) {
        setUpdateViewLambda()
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<PodcastEpisodesRowBinding>) {
        removeUpdateViewLambda() //remove callback so that recycled viewholder doesn't undeservingly get updates
        super.onViewDetachedFromWindow(holder)
    }


    override fun getItemsCount(data: List<Data>): Int {
        return data.size;
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setUpdateViewLambda() {
        mutableMapOfIDToProgressListenerCallback[currentItem?.videoID]?.setLabmda {
            databinding.progressDownload.progress = toPercent(it).toInt()
        }
    }

    fun removeUpdateViewLambda() {
        mutableMapOfIDToProgressListenerCallback[currentItem?.videoID]?.setLabmda {}
    }

    fun downloadEpisode() {
        val audio = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .toString(), "file_example_MP3_700KB.mp3"
        )
        Thread { //use coroutines instead
            downloadItem(
                currentItem,
                0,
                this@PodEpisodesAdapter,
                "https://file-examples.com/storage/fe1e98be1065198f8a74ddc/2017/11/file_example_MP3_700KB.mp3", //insert your download here
                "${audio.path}",
                "file_example_MP3_700KB.mp3"
//                "a${currentItem?.videoID}.mp3"
            )
        }.start()
    }

}

class MyDownloadProgressListener : OnProgressListener {
    var listItem: Data? = null
    private var updateView: (progress: Progress) -> Unit = {}
    fun setLabmda(lambda: (progress: Progress) -> Unit) {
        updateView = lambda
    }

    override fun onProgress(progress: Progress?) {
        Log.d("Adapter", "Progress object: $progress")
        if (progress != null) {
            val toPercent = toPercent(progress)
            Log.d("Adapter", "Percent received: $toPercent")
            listItem?.percentDownloaded = toPercent
            updateView(progress)
        }
    }
}


fun downloadItem(
    listItem: Data?,
    adapterPosition: Int,
    adapter: PodEpisodesAdapter,
    url: String,
    dirPath: String,
    filename: String
) {
    val listener = MyDownloadProgressListener()
    mutableMapOfIDToProgressListenerCallback[listItem?.videoID] = listener
    if (listItem != null) {
        listener.listItem = listItem
    }
    Handler(Looper.getMainLooper()).post { adapter.notifyItemChanged(adapterPosition)/*bind listener to ViewHolder*/ }

    PRDownloader.download(url, dirPath, filename).build().setOnProgressListener(listener)
        .setOnCancelListener {
            mutableMapOfIDToProgressListenerCallback.remove(listItem?.videoID)
        }.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                mutableMapOfIDToProgressListenerCallback.remove(listItem?.videoID)
            }

            override fun onError(error: com.downloader.Error?) {
                mutableMapOfIDToProgressListenerCallback.remove(listItem?.videoID)
            }

        })
}

interface OnEpisodeClickListener {
    fun onEpisodeClicked(data: Data)
    fun onEpisodeDownloadClicked(data: Data)
    fun onEpisodeDeleteClicked(data: Data)
    fun onEpisodeShareClicked(data: Data)
}