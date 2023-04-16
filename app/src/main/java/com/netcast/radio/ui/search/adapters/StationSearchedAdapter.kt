package com.netcast.radio.ui.search.adapters

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.StationSearchRowBinding
import com.netcast.radio.ui.search.searchedstationresponce.Data

class StationSearchedAdapter(private val list:List<Data>, val  stationSearchListener: StationSearchListener) :
    BaseAdapter<StationSearchRowBinding,Data>(list){
    override val layoutId: Int = R.layout.station_search_row

    override fun bind(binding: StationSearchRowBinding, item: Data) {
        binding.apply {
            searchstation = item
            listener = stationSearchListener
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
       return data.size;
    }
}

interface StationSearchListener{
    fun onStationSearchListener(data : Data)
}
