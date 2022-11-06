package com.coderoids.radio.ui.radioplayermanager.adapter

import com.coderoids.radio.R
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
    }
    override fun getItemsCount(data: List<Data>): Int {
        if(data.size >5)
            return 5;
        else
            return data.size;
    }
}

interface OnEpisodeClickListener {
    fun onEpisodeClicked(data: Data)
    fun onEpisodeDownloadClicked(data: Data)
}