package com.netcast.baidutv.ui.podcast.adapter


import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.PodcastRowBinding
import com.netcast.baidutv.ui.podcast.poddata.PodListData

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
//        return data.size
        return 10
    }

}
interface OnClickListenerPodcast {
    fun onPodCastClicked(data: PodListData)
}