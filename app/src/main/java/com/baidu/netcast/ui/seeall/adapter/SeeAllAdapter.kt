package com.baidu.netcast.ui.seeall.adapter

import com.baidu.netcast.R
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.SeeAllRowBinding
import com.baidu.netcast.ui.radio.data.temp.RadioLists

class SeeAllAdapter(
    private val list: List<RadioLists>,
    private val onClickListenerSeeAll: OnClickListenerSeeAll,
    private val type: String?
) : BaseAdapter<SeeAllRowBinding, RadioLists>(list) {
    override val layoutId: Int = R.layout.see_all_row

    override fun bind(binding: SeeAllRowBinding, item: RadioLists, position: Int) {
        binding.apply {
            radioLists = item
            title=type
            listener = onClickListenerSeeAll
            executePendingBindings()
        }
        checkIfItemisInFav(binding,item)
    }
    private fun checkIfItemisInFav(binding: SeeAllRowBinding, item: RadioLists) {
        if (AppSingelton.favouritesRadioArray != null) {
            AppSingelton.favouritesRadioArray.forEachIndexed { index, playingChannelData ->
                val id = playingChannelData.id
                if (item.id.toRegex()
                        ?.let { id.matches(it) } == true
                ) {
                    binding.imageShare.setImageResource(R.drawable.ic_fav_filled_24)
                }
            }
        }

    }
    override fun getItemsCount(data: List<RadioLists>): Int {
            return data.size
    }
}
interface OnClickListenerSeeAll {
    fun onSeeAllClick(data: RadioLists)
    fun onSeeAllFavClick(data: RadioLists)
}
