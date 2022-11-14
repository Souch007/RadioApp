package com.netcast.radio.ui.search.adapters

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.PodcastSearchRowBinding
import com.netcast.radio.ui.search.searchedpodresponce.Data

class PodcastSearchedAdapter(private val list : List<Data>,val  podcastSearchedAdapter: PodSearchOnClickListener)
    :BaseAdapter<PodcastSearchRowBinding,Data>(list){
    override val layoutId: Int = R.layout.podcast_search_row
    override fun bind(binding: PodcastSearchRowBinding, item: Data) {
        binding.apply {
            podlist = item
            listener = podcastSearchedAdapter
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        return data.size;
    }
}

interface PodSearchOnClickListener{
    fun onPodCastSearchedListener(data : Data)
}