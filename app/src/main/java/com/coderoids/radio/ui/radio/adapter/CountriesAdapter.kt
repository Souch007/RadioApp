package com.coderoids.radio.ui.radio.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.CountriesRowBinding
import com.coderoids.radio.ui.radio.countries.Data

class CountriesAdapter (
    private val list: List<Data>,
    private val onClickListenerCountires: OnClickListenerCountires
) : BaseAdapter<CountriesRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.countries_row

    override fun bind(binding: CountriesRowBinding, item: Data) {
        binding.apply {
            languageList = item
            listener = onClickListenerCountires
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        if(data.size > 10)
            return 9;
        else
            return data.size;
    }

}
interface OnClickListenerCountires {
    fun OnCountrlySelected(data: Data)
}