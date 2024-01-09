package com.netcast.baidutv.ui.radio.adapter

import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.CountriesRowBinding
import com.netcast.baidutv.ui.radio.countries.Data

class CountriesAdapter (
    private val list: List<Data>,
    private val onClickListenerCountires: OnClickListenerCountires,
    private val  type:String
) : BaseAdapter<CountriesRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.countries_row

    override fun bind(binding: CountriesRowBinding, item: Data, position: Int) {
        binding.apply {
            languageList = item
            listener = onClickListenerCountires
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        return if (type=="all")
            data.size
        else {
            if (data.size > 10)
                return 10
            else
                data.size
        }
    }

}
interface OnClickListenerCountires {
    fun OnCountrlySelected(data: Data)
}