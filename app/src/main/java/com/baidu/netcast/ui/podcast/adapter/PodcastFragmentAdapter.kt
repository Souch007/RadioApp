package com.baidu.netcast.ui.podcast.adapter


import com.baidu.netcast.R
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.PodcastRowBinding
import com.baidu.netcast.ui.podcast.poddata.PodListData

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