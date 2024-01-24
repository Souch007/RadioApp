package com.baidu.netcast.ui.radio.adapter

import com.baidu.netcast.R
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.RadioRowBinding
import com.baidu.netcast.ui.radio.data.temp.RadioLists

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
        return if(data.size > 10)
            16
        else
            data.size
    }
}
interface OnClickListnerRadio {
    fun onRadioClicked(data: RadioLists,type: String)
}