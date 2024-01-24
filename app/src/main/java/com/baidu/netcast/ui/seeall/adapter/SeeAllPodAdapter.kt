package com.baidu.netcast.ui.seeall.adapter

import com.baidu.netcast.R
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.SeeAllPodRowBinding
import com.baidu.netcast.ui.podcast.poddata.PodListData

class SeeAllPodAdapter(
    private val list: List<PodListData>,
    private val onCLickListenerPODSeeAll: OnClickListerPODSeeAll,
    private val type: String?
) : BaseAdapter<SeeAllPodRowBinding, PodListData>(list){
    override val layoutId: Int = R.layout.see_all_pod_row

    override fun bind(binding: SeeAllPodRowBinding, item: PodListData, position: Int) {
        binding.apply {
            podlist = item
            title= type
            listener = onCLickListenerPODSeeAll
            executePendingBindings()
        }
        checkIfItemisInFav(binding,item)
    }

    private fun checkIfItemisInFav(binding: SeeAllPodRowBinding, item: PodListData) {
        if (AppSingelton.favouritesRadioArray != null) {
            AppSingelton.favouritesRadioArray.forEachIndexed { index, playingChannelData ->
                val id = playingChannelData.id
                if (item._id.toRegex()
                        ?.let { id.matches(it) } == true
                ) {
                    binding.imageShare.setImageResource(R.drawable.ic_fav_filled_24)
                }
            }
        }

    }

    override fun getItemsCount(data: List<PodListData>): Int {
        return data.size;
    }

}
interface OnClickListerPODSeeAll {
    fun onPodClicked(data: PodListData)
    fun onSeeALlPodFavouriteClicked(data: PodListData)
}