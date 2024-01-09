package com.netcast.baidutv.ui.radio.adapter

import com.netcast.baidutv.PlayingChannelData
import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.ItemRecyclerAlaramitemBinding

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