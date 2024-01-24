package com.baidu.netcast.ui.radio

import com.baidu.netcast.R
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.FilterStationRowBinding
import com.baidu.netcast.ui.radio.data.temp.RadioLists


class FilterStationAdapter (private val list:List<RadioLists>, val  stationSearchListener: FilterSearchListener) :
    BaseAdapter<FilterStationRowBinding, RadioLists>(list){
    override val layoutId: Int = R.layout.filter_station_row

    override fun bind(binding: FilterStationRowBinding, item: RadioLists, position: Int) {
        binding.apply {
            searchstation = item
            listener = stationSearchListener
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<RadioLists>): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}


interface FilterSearchListener{
    fun onFilterSearchListenerr(data : RadioLists)
}