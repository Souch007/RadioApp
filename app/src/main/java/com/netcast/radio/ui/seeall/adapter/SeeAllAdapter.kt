package com.netcast.radio.ui.seeall.adapter

import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.SeeAllRowBinding
import com.netcast.radio.ui.radio.data.temp.RadioLists

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
