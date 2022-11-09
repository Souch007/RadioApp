package com.coderoids.radio.ui.radioplayermanager.adapter

import android.animation.ObjectAnimator
import com.coderoids.radio.R
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.PodcastEpisodesRowBinding
import com.coderoids.radio.databinding.RadioRowBinding
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data

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
            binding.tvDownlaodTag.text = "Offline Available"
        }

        if(AppSingelton.currentDownloading.matches(item._id.toString().toRegex())){
            binding.tvDownlaodTag.text = "Downloading..."
        }

    }
    override fun getItemsCount(data: List<Data>): Int {
            return data.size;
    }
}

interface OnEpisodeClickListener {
    fun onEpisodeClicked(data: Data)
    fun onEpisodeDownloadClicked(data: Data)
}