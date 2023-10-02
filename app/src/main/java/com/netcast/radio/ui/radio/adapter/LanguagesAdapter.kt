package com.netcast.radio.ui.radio.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.LanguagesRowBinding
import com.netcast.radio.ui.radio.lanuages.Data

class LanguagesAdapter(
    private val list: List<Data>,
    private val onClickListenerLanguages: OnClickListenerLanguages,
    private val type:String
) : BaseAdapter<LanguagesRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.languages_row

    override fun bind(binding: LanguagesRowBinding, item: Data, position: Int) {
        binding.apply {
            languageList = item
            listener = onClickListenerLanguages
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

interface OnClickListenerLanguages {
    fun onLanguageClicked(data: Data)
}