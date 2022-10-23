package com.coderoids.radio.ui.seeall.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.RadioRowBinding
import com.coderoids.radio.databinding.SeeAllRowBinding
import com.coderoids.radio.ui.radio.data.temp.RadioLists

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