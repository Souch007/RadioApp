package com.netcast.radio.ui.radioplayermanager.adapter

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.util.Log
import android.view.View
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.PodcastEpisodesRowBinding
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

class PodEpisodesAdapter(private val list: List<Data>,
                         private val _onEpisodeListener: OnEpisodeClickListener,private val  context: Context
) : BaseAdapter<PodcastEpisodesRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.podcast_episodes_row

    @SuppressLint("Range")
    override fun bind(binding: PodcastEpisodesRowBinding, item: Data) {
        binding.apply {
            episodeData = item
            listener = _onEpisodeListener
            executePendingBindings()
        }
        if(AppSingelton.downloadedIds.contains(item.id.toRegex())){
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

        if(AppSingelton.currentDownloading!!.matches(item.id.toRegex())){
//            binding.progressDownload.visibility = View.VISIBLE
//            binding.tvDownlaodTag.text = "Downloading..."
//            binding.tvDownlaodTag.visibility = View.VISIBLE
            binding.icDone.visibility = View.GONE
            binding.icDownlaod.visibility = View.GONE
        }

        Log.d("VIDEO_ID", "bind: ${item.videoID}")

        if (item.videoID!=0L) {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(item.videoID)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {

                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val progress = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                if (status == DownloadManager.STATUS_RUNNING) {
                    Log.d("PROGRESSSBARRRR", "progress: $progress")
                    // Download is in progress
                    binding.progressDownload.visibility = View.VISIBLE
                    binding.progressDownload.max =total
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
        }


    }
    override fun getItemsCount(data: List<Data>): Int {
            return data.size;
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}

interface OnEpisodeClickListener {
    fun onEpisodeClicked(data: Data)
    fun onEpisodeDownloadClicked(data: Data)
    fun onEpisodeDeleteClicked(data: Data)
    fun onEpisodeShareClicked(data: Data)
}