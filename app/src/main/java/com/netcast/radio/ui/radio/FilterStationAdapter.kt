package com.netcast.radio.ui.radio

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.FilterStationRowBinding
import com.netcast.radio.databinding.StationSearchRowBinding
import com.netcast.radio.ui.radio.data.temp.RadioLists


class FilterStationAdapter (private val list:List<RadioLists>, val  stationSearchListener: FilterSearchListener) :
    BaseAdapter<FilterStationRowBinding, RadioLists>(list){
    override val layoutId: Int = R.layout.filter_station_row

    override fun bind(binding: FilterStationRowBinding, item: RadioLists) {
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