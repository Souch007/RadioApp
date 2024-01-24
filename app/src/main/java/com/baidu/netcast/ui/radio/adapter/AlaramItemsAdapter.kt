package com.baidu.netcast.ui.radio.adapter

import com.baidu.netcast.PlayingChannelData
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.ItemRecyclerAlaramitemBinding

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