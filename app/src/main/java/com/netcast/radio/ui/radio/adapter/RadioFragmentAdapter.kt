package com.netcast.radio.ui.radio.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.RadioRowBinding
import com.netcast.radio.ui.radio.data.temp.RadioLists

class RadioFragmentAdapter(
    private val list: List<RadioLists>,
    private val OnClickListnerRadio: OnClickListnerRadio,
    private val selectiontype: String
) : BaseAdapter<RadioRowBinding, RadioLists>(list) {
    override val layoutId: Int = R.layout.radio_row

    override fun bind(binding: RadioRowBinding, item: RadioLists, position: Int) {
        binding.apply {
            radioLists = item
            type=selectiontype
            listener = OnClickListnerRadio
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<RadioLists>): Int {
        return if(data.size > 5)
            5
        else
            data.size
    }
}
interface OnClickListnerRadio {
    fun onRadioClicked(data: RadioLists,type: String)
}