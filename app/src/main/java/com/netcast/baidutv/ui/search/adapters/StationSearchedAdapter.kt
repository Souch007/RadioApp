package com.netcast.baidutv.ui.search.adapters

import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.StationSearchRowBinding
import com.netcast.baidutv.ui.search.searchedstationresponce.Data

class StationSearchedAdapter(private val list:List<Data>, val  stationSearchListener: StationSearchListener) :
    BaseAdapter<StationSearchRowBinding,Data>(list){
    override val layoutId: Int = R.layout.station_search_row

    override fun bind(binding: StationSearchRowBinding, item: Data, position: Int) {
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
