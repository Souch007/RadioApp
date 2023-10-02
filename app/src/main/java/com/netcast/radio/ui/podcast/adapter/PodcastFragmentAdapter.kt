package com.netcast.radio.ui.podcast.adapter


import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.PodcastRowBinding
import com.netcast.radio.ui.podcast.poddata.PodListData

class PodcastFragmentAdapter(
    private val list: List<PodListData>,
    private val _onClickListenerPodcast: OnClickListenerPodcast
) : BaseAdapter<PodcastRowBinding, PodListData>(list) {
    override val layoutId: Int = R.layout.podcast_row

    override fun bind(binding: PodcastRowBinding, item: PodListData, position: Int) {
        binding.apply {
            feeds = item
            listener = _onClickListenerPodcast
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<PodListData>): Int {
        return data.size;
    }

}
interface OnClickListenerPodcast {
    fun onPodCastClicked(data: PodListData)
}