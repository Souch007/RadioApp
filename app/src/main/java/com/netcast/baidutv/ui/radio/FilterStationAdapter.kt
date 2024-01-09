package com.netcast.baidutv.ui.radio

import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.FilterStationRowBinding
import com.netcast.baidutv.ui.radio.data.temp.RadioLists


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