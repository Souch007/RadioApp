package com.netcast.radio.ui.radio.adapter

import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.ItemRecyclerAlaramitemBinding
import com.netcast.radio.databinding.RadioRowBinding
import com.netcast.radio.ui.radio.data.temp.RadioLists

class AlaramItemsAdapter(
    private val list: List<PlayingChannelData>,
    private val OnClickListner: OnClickAlarmItem,
    private val selectiontype: String
) : BaseAdapter<ItemRecyclerAlaramitemBinding, PlayingChannelData>(list) {
    override val layoutId: Int = R.layout.item_recycler_alaramitem

    override fun bind(binding: ItemRecyclerAlaramitemBinding, item: PlayingChannelData, position: Int) {
        binding.apply {
            data = item
            pos=position
            listener = OnClickListner
            executePendingBindings()
        }
        binding.checkbox.isChecked = item.isSelected

    }

    override fun getItemsCount(data: List<PlayingChannelData>): Int {
           return data.size
    }
  fun setSelection(){

  }
}


interface OnClickAlarmItem{
    fun onAlramItemClicked(data: PlayingChannelData,position: Int)
}