package com.coderoids.radio.ui.radio.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.RadioRowBinding
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.radio.lanuages.Data

class RadioFragmentAdapter(
    private val list: List<RadioLists>,
    private val OnClickListnerRadio: OnClickListnerRadio
) : BaseAdapter<RadioRowBinding, RadioLists>(list) {
    override val layoutId: Int = R.layout.radio_row

    override fun bind(binding: RadioRowBinding, item: RadioLists) {
        binding.apply {
            radioLists = item
            listener = OnClickListnerRadio
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<RadioLists>): Int {
        return data.size;
    }
}
interface OnClickListnerRadio {
    fun onRadioClicked(data: RadioLists)
}