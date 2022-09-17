package com.coderoids.radio.ui.radio.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.RadioRowBinding
import com.coderoids.radio.ui.radio.data.Data

class RadioFragmentAdapter(
    private val list: List<Data>,
    private val OnClickListnerRadio: OnClickListnerRadio
) : BaseAdapter<RadioRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.radio_row

    override fun bind(binding: RadioRowBinding, item: Data) {
        binding.apply {
            data = item
            listener = OnClickListnerRadio
            executePendingBindings()
        }
    }
}
interface OnClickListnerRadio {
    fun onRadioClicked(data: Data)
}