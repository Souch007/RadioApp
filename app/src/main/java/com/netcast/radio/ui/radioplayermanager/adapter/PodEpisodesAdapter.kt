package com.netcast.radio.ui.radioplayermanager.adapter

import android.view.View
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.PodcastEpisodesRowBinding
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

class PodEpisodesAdapter(private val list: List<Data>,
                         private val _onEpisodeListener: OnEpisodeClickListener
) : BaseAdapter<PodcastEpisodesRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.podcast_episodes_row

    override fun bind(binding: PodcastEpisodesRowBinding, item: Data) {
        binding.apply {
            episodeData = item
            listener = _onEpisodeListener
            executePendingBindings()
        }
        if(AppSingelton.downloadedIds.contains(item._id.toString().toRegex())){
//            binding.tvDownlaodTag.text = "Offline Available"
            binding.icDone.visibility = View.VISIBLE
            binding.icDownlaod.visibility = View.GONE
            binding.progressDownload.visibility = View.GONE

        }

        if(AppSingelton.currentDownloading.matches(item._id.toString().toRegex())){
            binding.progressDownload.visibility = View.VISIBLE
//            binding.tvDownlaodTag.text = "Downloading..."
//            binding.tvDownlaodTag.visibility = View.VISIBLE
            binding.icDone.visibility = View.GONE
            binding.icDownlaod.visibility = View.GONE

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
}