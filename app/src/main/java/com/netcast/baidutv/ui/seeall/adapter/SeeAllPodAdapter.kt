package com.netcast.baidutv.ui.seeall.adapter

import com.netcast.baidutv.R
import com.netcast.baidutv.base.AppSingelton
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.SeeAllPodRowBinding
import com.netcast.baidutv.ui.podcast.poddata.PodListData

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