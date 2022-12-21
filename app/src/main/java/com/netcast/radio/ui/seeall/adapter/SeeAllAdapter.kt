package com.netcast.radio.ui.seeall.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.SeeAllRowBinding
import com.netcast.radio.ui.radio.data.temp.RadioLists

class SeeAllAdapter (
    private val list: List<RadioLists>,
    private val onClickListenerSeeAll: OnClickListenerSeeAll
) : BaseAdapter<SeeAllRowBinding, RadioLists>(list) {
    override val layoutId: Int = R.layout.see_all_row

    override fun bind(binding: SeeAllRowBinding, item: RadioLists) {
        binding.apply {
            radioLists = item
            listener = onClickListenerSeeAll
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<RadioLists>): Int {
            return data.size;
    }
}
interface OnClickListenerSeeAll {
    fun onSeeAllClick(data: RadioLists)
}