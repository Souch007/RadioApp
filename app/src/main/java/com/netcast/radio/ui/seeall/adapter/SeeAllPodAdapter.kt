package com.netcast.radio.ui.seeall.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.SeeAllPodRowBinding
import com.netcast.radio.ui.podcast.poddata.PodListData

class SeeAllPodAdapter (
    private val list: List<PodListData>,
    private val onCLickListenerPODSeeAll: OnClickListerPODSeeAll
) : BaseAdapter<SeeAllPodRowBinding, PodListData>(list){
    override val layoutId: Int = R.layout.see_all_pod_row

    override fun bind(binding: SeeAllPodRowBinding, item: PodListData) {
        binding.apply {
            podlist = item
            listener = onCLickListenerPODSeeAll
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<PodListData>): Int {
        return data.size;
    }

}
interface OnClickListerPODSeeAll {
    fun onPodClicked(data: PodListData)
}