package com.coderoids.radio.ui.seeall.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.SeeAllPodRowBinding
import com.coderoids.radio.databinding.SeeAllRowBinding
import com.coderoids.radio.ui.podcast.poddata.PodListData
import com.coderoids.radio.ui.radio.data.temp.RadioLists

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