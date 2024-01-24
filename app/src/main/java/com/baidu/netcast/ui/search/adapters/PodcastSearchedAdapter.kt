package com.baidu.netcast.ui.search.adapters

import com.baidu.netcast.R
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.PodcastSearchRowBinding
import com.baidu.netcast.ui.search.searchedpodresponce.Data

class PodcastSearchedAdapter(private val list : List<Data>,val  podcastSearchedAdapter: PodSearchOnClickListener)
    :BaseAdapter<PodcastSearchRowBinding,Data>(list){
    override val layoutId: Int = R.layout.podcast_search_row
    override fun bind(binding: PodcastSearchRowBinding, item: Data, position: Int) {
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